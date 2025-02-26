import {
  type ConfigPlugin,
  IOSConfig,
  type Mod,
  withMod,
  BaseMods,
  type ExportedConfig,
} from '@expo/config-plugins';
import { globSync } from 'glob';
import * as fs from 'node:fs';

/**
 * Reference: https://github.com/expo/expo/blob/cb38337d37c35a26ac9a39eac2268c4735f488ad/packages/%40expo/config-plugins/src/ios/Paths.ts#L10C1-L10C69
 */
const ignoredPaths = ['**/@(Carthage|Pods|vendor|node_modules)/**'];

/**
 * This plugin adds a new mod to the prebuild that is the appBridgingHeader.
 * This allows us to update it when the user launches the prebuild script.
 */
export function withAppBridgingHeaderMod(config: ExportedConfig) {
  return BaseMods.withGeneratedBaseMods<'appBridgingHeader'>(config, {
    platform: 'ios',
    providers: {
      // Append a custom rule to supply AppDelegate header data to mods on `mods.ios.appBridgingHeader`
      appBridgingHeader:
        BaseMods.provider<IOSConfig.Paths.AppDelegateProjectFile>({
          // Get the local filepath that should be passed to the `read` method.
          getFilePath({ modRequest: { projectRoot } }) {
            return getBridgingHeaderFilePath(projectRoot);
          },
          // Read the input file from the filesystem.
          async read(filePath) {
            return IOSConfig.Paths.getFileInfo(filePath);
          },
          // Write the resulting output to the filesystem.
          async write(filePath: string, { modResults: { contents } }) {
            await fs.promises.writeFile(filePath, contents);
          },
        }),
    },
  });
}

/**
 * This mod provides the app bridging header for modifications
 * @param config
 * @param action
 */
export const withAppBridgingHeader: ConfigPlugin<
  Mod<IOSConfig.Paths.AppDelegateProjectFile>
> = (config, action) => {
  return withMod(config, {
    platform: 'ios',
    mod: 'appBridgingHeader',
    action,
  });
};

/**
 * Reference: https://github.com/expo/expo/blob/cb38337d37c35a26ac9a39eac2268c4735f488ad/packages/%40expo/config-plugins/src/ios/Paths.ts#L23
 * @param projectRoot
 */
function getBridgingHeaderFilePath(projectRoot: string) {
  const [using, ...extra] = withSortedGlobResult(
    globSync('ios/*/*-Bridging-Header.h', {
      absolute: true,
      cwd: projectRoot,
      ignore: ignoredPaths,
    })
  );

  if (!using) {
    throw new Error(
      `Could not locate a valid Bridging-Header at root: "${projectRoot}"`
    );
  }

  if (extra.length) {
    throw new Error(`Multiple Bridging-Header found at root: "${projectRoot}"`);
  }

  return using;
}

/**
 * Reference: https://github.com/expo/expo/blob/main/packages/%40expo/config-plugins/src/utils/glob.ts
 */
function withSortedGlobResult(glob: string[]) {
  return glob.sort((a, b) => a.localeCompare(b));
}
