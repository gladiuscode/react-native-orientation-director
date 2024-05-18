import { NativeModules, Platform } from 'react-native';
import type { Spec } from './NativeOrientationHandler';
import type InterfaceOrientation from './types/InterfaceOrientation.enum';

const LINKING_ERROR =
  `The package 'react-native-orientation-handler' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const OrientationHandlerModule = isTurboModuleEnabled
  ? require('./NativeOrientationHandler').default
  : NativeModules.OrientationHandler;

const OrientationHandler = (
  OrientationHandlerModule
    ? OrientationHandlerModule
    : new Proxy(
        {},
        {
          get() {
            throw new Error(LINKING_ERROR);
          },
        }
      )
) as Spec;

class RNOrientationHandler {
  static getInterfaceOrientation(): Promise<InterfaceOrientation> {
    return OrientationHandler.getInterfaceOrientation();
  }

  static lockTo(orientation: InterfaceOrientation) {
    OrientationHandler.lockTo(orientation);
  }
}

export default RNOrientationHandler;
