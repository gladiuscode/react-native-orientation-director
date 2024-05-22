import Module, { EventEmitter } from './module';
import Event from './types/Event.enum';
import type { InterfaceOrientationToLocalizedStringProvider } from './types/InterfaceOrientationToLocalizedStringProvider.type';
import { Orientation } from './types/Orientation.enum';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockableOrientation } from './types/LockableOrientation.type';

class RNOrientationDirector {
  private static _localizedStringProvider: InterfaceOrientationToLocalizedStringProvider =
    {
      [Orientation.unknown]: 'Unknown',
      [Orientation.portrait]: 'Portrait',
      [Orientation.portraitUpsideDown]: 'Portrait Upside Down',
      [Orientation.landscapeLeft]: 'Landscape Left',
      [Orientation.landscapeRight]: 'Landscape Right',
      [Orientation.faceUp]: 'Face Up',
      [Orientation.faceDown]: 'Face Down',
    };

  setLocalizedStringProvider(
    provider: InterfaceOrientationToLocalizedStringProvider
  ) {
    RNOrientationDirector._localizedStringProvider = provider;
  }

  static getInterfaceOrientation(): Promise<Orientation> {
    return Module.getInterfaceOrientation();
  }

  static getDeviceOrientation(): Promise<Orientation> {
    return Module.getDeviceOrientation();
  }

  static lockTo(orientation: LockableOrientation) {
    Module.lockTo(orientation);
  }

  static unlock() {
    Module.unlock();
  }

  static listenForDeviceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    return EventEmitter.addListener(Event.DeviceOrientationDidChange, callback);
  }

  static listenForInterfaceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    return EventEmitter.addListener(
      Event.InterfaceOrientationDidChange,
      callback
    );
  }

  static convertOrientationToHumanReadableString(
    orientation: Orientation
  ): string {
    return RNOrientationDirector._localizedStringProvider[orientation];
  }
}

export default RNOrientationDirector;
