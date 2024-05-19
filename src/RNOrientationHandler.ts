import Module, { EventEmitter } from './module';
import Event from './types/Event.enum';
import type { InterfaceOrientationToLocalizedStringProvider } from './types/InterfaceOrientationToLocalizedStringProvider.type';
import { Orientation } from './types/Orientation.enum';
import type { OrientationEvent } from './types/OrientationEvent.interface';

class RNOrientationHandler {
  private static _localizedStringProvider: InterfaceOrientationToLocalizedStringProvider =
    {
      [Orientation.unknown]: 'Unknown',
      [Orientation.portrait]: 'Portrait',
      [Orientation.portraitUpsideDown]: 'Portrait Upside Down',
      [Orientation.landscapeLeft]: 'Landscape Left',
      [Orientation.landscapeRight]: 'Landscape Right',
    };

  setLocalizedStringProvider(
    provider: InterfaceOrientationToLocalizedStringProvider
  ) {
    RNOrientationHandler._localizedStringProvider = provider;
  }

  static getInterfaceOrientation(): Promise<Orientation> {
    return Module.getInterfaceOrientation();
  }

  static getDeviceOrientation(): Promise<Orientation> {
    return Module.getDeviceOrientation();
  }

  static lockTo(orientation: Orientation) {
    Module.lockTo(orientation);
  }

  static listenForDeviceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    EventEmitter.addListener(Event.DeviceOrientationDidChange, callback);
  }

  static listenForInterfaceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    EventEmitter.addListener(Event.InterfaceOrientationDidChange, callback);
  }

  static convertOrientationToHumanReadableStrings(
    orientation: Orientation
  ): string {
    return RNOrientationHandler._localizedStringProvider[orientation];
  }
}

export default RNOrientationHandler;
