import {
  type ConfigPlugin,
  type ExportedConfigWithProps,
  withAppDelegate,
} from '@expo/config-plugins';
import { type AppDelegateProjectFile } from '@expo/config-plugins/build/ios/Paths';
import { mergeContents } from '@expo/config-plugins/build/utils/generateCode';

export const withRNOrientationAppDelegate: ConfigPlugin = (config) => {
  return withAppDelegate(config, readAppDelegateFileAndUpdateContents);
};

async function readAppDelegateFileAndUpdateContents(
  config: ExportedConfigWithProps<AppDelegateProjectFile>
): Promise<ExportedConfigWithProps<AppDelegateProjectFile>> {
  const { modResults: appDelegateFile } = config;

  const fileUpdater = getCompatibleFileUpdater(appDelegateFile.language);
  if (fileUpdater.language === 'swift') {
    const { worker } = fileUpdater;
    appDelegateFile.contents = worker(
      appDelegateFile.contents,
      config.sdkVersion
    );
  } else {
    const { worker } = fileUpdater;
    appDelegateFile.contents = worker(appDelegateFile.contents);
  }

  return config;
}

function getCompatibleFileUpdater(
  language: AppDelegateProjectFile['language']
):
  | { language: 'swift'; worker: typeof swiftFileUpdater }
  | { language: 'objc' | 'objcpp'; worker: typeof objCFileUpdater } {
  switch (language) {
    case 'objc':
    case 'objcpp': {
      return {
        language,
        worker: objCFileUpdater,
      };
    }
    case 'swift':
      return {
        language,
        worker: swiftFileUpdater,
      };
    default:
      throw new Error(
        `Cannot add React Native Orientation Director code to AppDelegate of language "${language}"`
      );
  }
}

export function swiftFileUpdater(
  originalContents: string,
  sdkVersion?: string
): string {
  const methodPrefix = !sdkVersion?.includes('53') ? 'override' : '';
  const supportedInterfaceOrientationsForCodeBlock = `\n  ${methodPrefix} func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
    return OrientationDirector.getSupportedInterfaceOrientationsForWindow()
  }\n`;
  const rightBeforeLastClosingBrace =
    /didFinishLaunchingWithOptions:\s*launchOptions\)/g;
  const pasteInTheListJustAfterTheClosingBracket = 2;

  const results = mergeContents({
    tag: '@react-native-orientation-director/supportedInterfaceOrientationsFor-implementation',
    src: originalContents,
    newSrc: supportedInterfaceOrientationsForCodeBlock,
    anchor: rightBeforeLastClosingBrace,
    offset: pasteInTheListJustAfterTheClosingBracket,
    comment: '// React Native Orientation Director',
  });

  return results.contents;
}

export function objCFileUpdater(originalContents: string): string {
  const libraryHeaderImportCodeBlock = '#import "OrientationDirector.h"\n';
  const rightBeforeAppDelegateImplementation = /@implementation\s+\w+/g;

  const headerImportMergeResults = mergeContents({
    tag: '@react-native-orientation-director/library-header-import',
    src: originalContents,
    newSrc: libraryHeaderImportCodeBlock,
    anchor: rightBeforeAppDelegateImplementation,
    offset: 0,
    comment: '// React Native Orientation Director',
  });

  const supportedInterfaceOrientationsForCodeBlock = `- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
  return [OrientationDirector getSupportedInterfaceOrientationsForWindow];
}\n`;
  const rightBeforeLastClosingEnd = /@end[^@]*$/g;

  const implementationMergeResults = mergeContents({
    tag: '@react-native-orientation-director/supportedInterfaceOrientationsFor-implementation',
    src: headerImportMergeResults.contents,
    newSrc: supportedInterfaceOrientationsForCodeBlock,
    anchor: rightBeforeLastClosingEnd,
    offset: 0,
    comment: '// React Native Orientation Director',
  });

  return implementationMergeResults.contents;
}
