import {
  ConfigPlugin,
  ExportedConfigWithProps,
  withAppDelegate,
} from '@expo/config-plugins';
import { AppDelegateProjectFile } from '@expo/config-plugins/build/ios/Paths';
import { mergeContents } from '@expo/config-plugins/build/utils/generateCode';

export const withRNOrientationAppDelegate: ConfigPlugin = (config) => {
  return withAppDelegate(config, readAppDelegateFileAndUpdateContents);
};

async function readAppDelegateFileAndUpdateContents(
  config: ExportedConfigWithProps<AppDelegateProjectFile>
): Promise<ExportedConfigWithProps<AppDelegateProjectFile>> {
  const { modResults: appDelegateFile } = config;

  const worker = getCompatibleFileUpdater(appDelegateFile.language);
  appDelegateFile.contents = worker(appDelegateFile);

  return config;
}

function getCompatibleFileUpdater(
  language: AppDelegateProjectFile['language']
): (file: AppDelegateProjectFile) => string {
  switch (language) {
    case 'objc':
    case 'objcpp': {
      return objCFileUpdater;
    }
    case 'swift':
      return swiftFileUpdater;
    default:
      throw new Error(
        `Cannot add React Native Orientation Director code to AppDelegate of language "${language}"`
      );
  }
}

function swiftFileUpdater(file: AppDelegateProjectFile): string {
  const supportedInterfaceOrientationsForCodeBlock = `override func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
  return OrientationDirector.getSupportedInterfaceOrientationsForWindow()
}\n`;
  const rightBeforeLastClosingBrace = /\}[^}]*$/g;

  const results = mergeContents({
    tag: '@react-native-orientation-director/supportedInterfaceOrientationsFor-implementation',
    src: file.contents,
    newSrc: supportedInterfaceOrientationsForCodeBlock,
    anchor: rightBeforeLastClosingBrace,
    offset: 0,
    comment: '// React Native Orientation Director',
  });

  return results.contents;
}

function objCFileUpdater(file: AppDelegateProjectFile): string {
  const libraryHeaderImportCodeBlock = '#import <OrientationDirector.h>\n';
  const rightBeforeAppDelegateImplementation = /@implementation\s+\w+/g;

  const headerImportMergeResults = mergeContents({
    tag: '@react-native-orientation-director/library-header-import',
    src: file.contents,
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
