import {
  type ConfigPlugin,
  createRunOncePlugin,
  withPlugins,
} from '@expo/config-plugins';

import { withAppBridgingHeaderMod } from './custom-mod/withBridgingHeader';
import { withRNOrientationAppDelegate } from './withRNOrientationAppDelegate';
import { withRNOrientationBridgingHeader } from './withRNOrientationBridgingHeader';
import { withRNOrientationMainActivity } from './withRNOrientationMainActivity';

/**
 * So, expo config plugin are awesome and the documentation is well written, but I still needed to look around to see
 * how other projects actually modify the AppDelegate. I've found react-native-firebase to implement a plugin config
 * that changes the AppDelegate, so I'll leave their link as reference:
 * https://github.com/invertase/react-native-firebase/blob/main/packages/app/plugin/src/ios/appDelegate.ts
 *
 * Kudos to them, because this stuff is hard!
 *
 * @param config
 */
const withRNOrientationDirector: ConfigPlugin = (config) => {
  return withPlugins(config, [
    withRNOrientationAppDelegate,
    withRNOrientationBridgingHeader,
    withRNOrientationMainActivity,
    withAppBridgingHeaderMod,
  ]);
};

const pak = require('react-native-orientation-director/package.json');
export default createRunOncePlugin(
  withRNOrientationDirector,
  pak.name,
  pak.version
);
