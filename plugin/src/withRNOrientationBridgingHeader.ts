import {
  type ConfigPlugin,
  type ExportedConfigWithProps,
  IOSConfig,
} from '@expo/config-plugins';
import { type AppDelegateProjectFile } from '@expo/config-plugins/build/ios/Paths';

import { withAppBridgingHeader } from './custom-mod/withBridgingHeader';

export const withRNOrientationBridgingHeader: ConfigPlugin = (config) => {
  return withAppBridgingHeader(config, readBridgingHeaderFileAndUpdateContents);
};

async function readBridgingHeaderFileAndUpdateContents(
  config: ExportedConfigWithProps<AppDelegateProjectFile>
) {
  const { projectRoot } = config.modRequest;
  if (isObjCTemplate(projectRoot)) {
    return config;
  }

  const { modResults: bridgingHeaderFile } = config;

  bridgingHeaderFile.contents = bridgingHeaderUpdater(
    bridgingHeaderFile.contents
  );

  return config;
}

export function bridgingHeaderUpdater(originalContents: string) {
  const libraryHeaderImport = '#import "OrientationDirector.h"';

  return originalContents.concat(`\n${libraryHeaderImport}`);
}

function isObjCTemplate(projectRoot: string) {
  const appDelegateFile = IOSConfig.Paths.getAppDelegate(projectRoot);
  return appDelegateFile.language !== 'swift';
}
