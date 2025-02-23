import {ConfigPlugin, ExportedConfigWithProps, IOSConfig} from "@expo/config-plugins";
import {withAppBridgingHeader} from "./custom-mod/withBridgingHeader";
import {AppDelegateProjectFile} from "@expo/config-plugins/build/ios/Paths";

export const withRNOrientationBridgingHeader: ConfigPlugin = (config) => {
  return withAppBridgingHeader(config, readBridgingHeaderFileAndUpdateContents);
}

async function readBridgingHeaderFileAndUpdateContents(config: ExportedConfigWithProps<AppDelegateProjectFile>) {
  const {projectRoot} = config.modRequest;
  if (isObjCTemplate(projectRoot)) {
    return config;
  }

  const {modResults: bridgingHeaderFile} = config;

  const libraryHeaderImport = '#import "OrientationDirector.h"';

  bridgingHeaderFile.contents = bridgingHeaderFile.contents.concat(`\n${libraryHeaderImport}`);

  return config;
}

function isObjCTemplate(projectRoot: string) {
  const appDelegateFile = IOSConfig.Paths.getAppDelegate(projectRoot);
  return appDelegateFile.language !== 'swift';
}

