import { Platform } from 'react-native';
import Module, { EventEmitter } from './module';
import Event from './types/Event.enum';
import type { InterfaceOrientationToLocalizedStringProvider } from './types/InterfaceOrientationToLocalizedStringProvider.type';
import { Orientation } from './types/Orientation.enum';
import { AutoRotation } from './types/AutoRotation.enum';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockableOrientation } from './types/LockableOrientation.type';
import type { LockedEvent } from './types/LockedEvent.interface';

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

  static isLocked() {
    return Module.isLocked();
  }

  static isAutoRotationEnabled(): AutoRotation {
    if (Platform.OS !== 'android') {
      return AutoRotation.unknown;
    }
    return Module.isAutoRotationEnabled()
      ? AutoRotation.enabled
      : AutoRotation.disabled;
  }

  static resetSupportedInterfaceOrientations(): void {
    Module.resetSupportedInterfaceOrientations();
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

  static listenForLockChanges(callback: (orientation: LockedEvent) => void) {
    return EventEmitter.addListener(Event.LockDidChange, callback);
  }

  static convertOrientationToHumanReadableString(
    orientation: Orientation
  ): string {
    return RNOrientationDirector._localizedStringProvider[orientation];
  }
}

export default RNOrientationDirector;
