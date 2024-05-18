import InterfaceOrientation from './types/InterfaceOrientation.enum';
import Module, { EventEmitter } from './module';
import type { DeviceOrientationChangesEvent } from './types/DeviceOrientationChangesEvent.interface';
import Event from './types/Event.enum';
import type { InterfaceOrientationChangesEvent } from './types/InterfaceOrientationChangesEvent.interface';
import type { InterfaceOrientationToLocalizedStringProvider } from './types/InterfaceOrientationToLocalizedStringProvider.type';

class RNOrientationHandler {
  private static _localizedStringProvider: InterfaceOrientationToLocalizedStringProvider =
    {
      [InterfaceOrientation.unknown]: 'Unknown',
      [InterfaceOrientation.portrait]: 'Portrait',
      [InterfaceOrientation.portraitUpsideDown]: 'Portrait Upside Down',
      [InterfaceOrientation.landscapeLeft]: 'Landscape Left',
      [InterfaceOrientation.landscapeRight]: 'Landscape Right',
    };

  setLocalizedStringProvider(
    provider: InterfaceOrientationToLocalizedStringProvider
  ) {
    RNOrientationHandler._localizedStringProvider = provider;
  }

  static getInterfaceOrientation(): Promise<InterfaceOrientation> {
    return Module.getInterfaceOrientation();
  }

  static lockTo(orientation: InterfaceOrientation) {
    Module.lockTo(orientation);
  }

  static listenForDeviceOrientationChanges(
    callback: (orientation: DeviceOrientationChangesEvent) => void
  ) {
    EventEmitter.addListener(Event.DeviceOrientationDidChange, callback);
  }

  static listenForInterfaceOrientationChanges(
    callback: (orientation: InterfaceOrientationChangesEvent) => void
  ) {
    EventEmitter.addListener(Event.InterfaceOrientationDidChange, callback);
  }

  static convertInterfaceOrientationToHumanReadableString(
    orientation: InterfaceOrientation
  ): string {
    return RNOrientationHandler._localizedStringProvider[orientation];
  }
}

export default RNOrientationHandler;
