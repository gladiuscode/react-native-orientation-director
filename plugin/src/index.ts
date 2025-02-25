import { type ConfigPlugin, withPlugins } from '@expo/config-plugins';

import { withAppBridgingHeaderMod } from './custom-mod/withBridgingHeader';
import { withRNOrientationAppDelegate } from './withRNOrientationAppDelegate';
import { withRNOrientationBridgingHeader } from './withRNOrientationBridgingHeader';

/**
 * So, expo config plugin are awesome and the documentation is well written, but I still needed to look around to see
 * how other projects actually modify the AppDelegate. I've found react-native-firebase to implement a plugin config
 * that changes the AppDelegate, so I'll leave their link as reference:
 * https://github.com/invertase/react-native-firebase/blob/main/packages/app/plugin/src/ios/appDelegate.ts
 *
 * Kudos to them, because this stuff is hard!
 *
 * @param config
 * @param name
 */
const withRNOrientationDirector: ConfigPlugin<{ name?: string }> = (
  config,
  { name = 'react-native-orientation-director' } = {}
) => {
  config.name = name;
  return withPlugins(config, [
    withRNOrientationAppDelegate,
    withRNOrientationBridgingHeader,
    withAppBridgingHeaderMod,
  ]);
};

export default withRNOrientationDirector;
