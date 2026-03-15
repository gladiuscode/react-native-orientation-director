import { type ConfigPlugin } from 'expo/config-plugins';
import { ExportedConfigWithProps, withAppDelegate } from '@expo/config-plugins';
import { AppDelegateProjectFile } from '@expo/config-plugins/build/ios/Paths';
import { mergeContents } from '@expo/config-plugins/build/utils/generateCode';

export const withIosConfiguration: ConfigPlugin = (config) => {
  return withAppDelegate(config, readAppDelegateFileAndUpdateContents);
};

async function readAppDelegateFileAndUpdateContents(
  config: ExportedConfigWithProps<AppDelegateProjectFile>
): Promise<ExportedConfigWithProps<AppDelegateProjectFile>> {
  const { modResults: appDelegateFile } = config;

  if (appDelegateFile.language !== 'swift') {
    throw new Error(
      `Cannot add React Native Orientation Director code to AppDelegate of language "${appDelegateFile.language}"`
    );
  }

  appDelegateFile.contents = swiftFileUpdater(
    appDelegateFile.contents,
    config.sdkVersion
  );

  return config;
}

export function swiftFileUpdater(
  originalContents: string,
  sdkVersion?: string
): string {
  const methodPrefix = computeMethodPrefix(sdkVersion);

  const libraryImportCodeBlock = `import OrientationDirector`;
  const rightBeforeAnnotation = /@main|@UIApplicationMain/g;
  const withImport = mergeContents({
    tag: '@react-native-orientation-director/library-import',
    src: originalContents,
    newSrc: libraryImportCodeBlock,
    anchor: rightBeforeAnnotation,
    offset: -1,
    comment: '// React Native Orientation Director',
  });

  const supportedInterfaceOrientationsCodeBlock = `\n  ${methodPrefix} func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
    return SharedOrientationDirectorImpl.shared.supportedInterfaceOrientations
  }\n`;
  const rightBeforeLastClosingBrace =
    /didFinishLaunchingWithOptions:\s*launchOptions\)/g;
  const pasteInTheListJustAfterTheClosingBracket = 2;
  const completedMerge = mergeContents({
    tag: '@react-native-orientation-director/supportedInterfaceOrientations-implementation',
    src: withImport.contents,
    newSrc: supportedInterfaceOrientationsCodeBlock,
    anchor: rightBeforeLastClosingBrace,
    offset: pasteInTheListJustAfterTheClosingBracket,
    comment: '// React Native Orientation Director',
  });

  return completedMerge.contents;

  function computeMethodPrefix(_sdkVersion?: string) {
    if (!_sdkVersion) {
      return '';
    }

    const rawMajor = _sdkVersion.split('.').at(0);
    if (!rawMajor) {
      return '';
    }

    const major = Number(rawMajor);
    if (Number.isNaN(major)) {
      return '';
    }

    if (major === 53) {
      return '';
    }

    return 'public override';
  }
}
