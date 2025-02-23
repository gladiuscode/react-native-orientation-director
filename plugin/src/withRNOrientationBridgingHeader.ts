import { ConfigPlugin, ExportedConfigWithProps } from '@expo/config-plugins';
import { withAppBridgingHeader } from './custom-mod/withBridgingHeader';
import { AppDelegateProjectFile } from '@expo/config-plugins/build/ios/Paths';

export const withRNOrientationBridgingHeader: ConfigPlugin = (config) => {
  return withAppBridgingHeader(config, readBridgingHeaderFileAndUpdateContents);
};

async function readBridgingHeaderFileAndUpdateContents(
  config: ExportedConfigWithProps<AppDelegateProjectFile>
) {
  const { modResults: bridgingHeaderFile } = config;

  const libraryHeaderImport = '#import "OrientationDirector.h"';

  bridgingHeaderFile.contents = bridgingHeaderFile.contents.concat(
    `\n${libraryHeaderImport}`
  );

  return config;
}
