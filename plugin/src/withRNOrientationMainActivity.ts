import {
  type ConfigPlugin,
  type ExportedConfigWithProps,
  withMainActivity,
} from '@expo/config-plugins';
import { type ApplicationProjectFile } from '@expo/config-plugins/build/android/Paths';
import { mergeContents } from '@expo/config-plugins/build/utils/generateCode';

export const withRNOrientationMainActivity: ConfigPlugin = (config) => {
  return withMainActivity(config, readMainActivityFileAndUpdateContents);
};

async function readMainActivityFileAndUpdateContents(
  config: ExportedConfigWithProps<ApplicationProjectFile>
): Promise<ExportedConfigWithProps<ApplicationProjectFile>> {
  const { modResults: mainActivityFile } = config;

  const worker = getCompatibleFileUpdater(mainActivityFile.language);
  mainActivityFile.contents = worker(mainActivityFile.contents);

  return config;
}

function getCompatibleFileUpdater(
  language: ApplicationProjectFile['language']
): (originalContents: string) => string {
  switch (language) {
    case 'kt':
      return ktFileUpdater;
    default:
      throw new Error(
        `Cannot add React Native Orientation Director code to MainActivity of language "${language}"`
      );
  }
}

export function ktFileUpdater(originalContents: string): string {
  const systemImportsContents =
    updateContentsWithSystemImports(originalContents);

  const libraryImportCodeBlock =
    'import com.orientationdirector.implementation.ConfigurationChangedBroadcastReceiver';
  const rightBeforeClassDeclaration = /class MainActivity/g;

  const importMergeResults = mergeContents({
    tag: '@react-native-orientation-director/library-import',
    src: systemImportsContents,
    newSrc: libraryImportCodeBlock,
    anchor: rightBeforeClassDeclaration,
    offset: 0,
    comment: '// React Native Orientation Director',
  });

  const onConfigurationChangedCodeBlock = `
  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)

    val orientationDirectorCustomAction =
      packageName + "." + ConfigurationChangedBroadcastReceiver.CUSTOM_INTENT_ACTION

    val intent =
      Intent(orientationDirectorCustomAction).apply {
        putExtra("newConfig", newConfig)
        setPackage(packageName)
      }

    this.sendBroadcast(intent)
  }\n`;

  const rightBeforeLastClosingBrace = /super\.onCreate/g;
  const pasteInTheListJustAfterTheClosingBracket = 2;

  const implementationMergeResults = mergeContents({
    tag: '@react-native-orientation-director/supportedInterfaceOrientationsFor-implementation',
    src: importMergeResults.contents,
    newSrc: onConfigurationChangedCodeBlock,
    anchor: rightBeforeLastClosingBrace,
    offset: pasteInTheListJustAfterTheClosingBracket,
    comment: '// React Native Orientation Director',
  });

  return implementationMergeResults.contents;
}

function updateContentsWithSystemImports(originalContents: string) {
  const rightBeforeClassDeclaration = /class MainActivity/g;

  let possibleUpdatedContents = originalContents;
  possibleUpdatedContents = addIntentImportIfNecessary(possibleUpdatedContents);
  possibleUpdatedContents = addConfigurationImportIfNecessary(
    possibleUpdatedContents
  );

  return possibleUpdatedContents;

  function addIntentImportIfNecessary(_contents: string) {
    const systemIntentImportCodeBlock = 'import android.content.Intent';
    if (_contents.includes(systemIntentImportCodeBlock)) {
      return _contents;
    }

    const mergeResults = mergeContents({
      tag: '@react-native-orientation-director/system-intent-import',
      src: _contents,
      newSrc: systemIntentImportCodeBlock,
      anchor: rightBeforeClassDeclaration,
      offset: 0,
      comment: '// React Native Orientation Director',
    });

    return mergeResults.contents;
  }
  function addConfigurationImportIfNecessary(_contents: string) {
    const systemConfigurationImportCodeBlock =
      'import android.content.res.Configuration';
    if (possibleUpdatedContents.includes(systemConfigurationImportCodeBlock)) {
      return _contents;
    }

    const mergeResults = mergeContents({
      tag: '@react-native-orientation-director/system-configuration-import',
      src: possibleUpdatedContents,
      newSrc: systemConfigurationImportCodeBlock,
      anchor: rightBeforeClassDeclaration,
      offset: 0,
      comment: '// React Native Orientation Director',
    });

    return mergeResults.contents;
  }
}
