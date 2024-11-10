import { Platform } from 'react-native';
import Module from './module';
import type { HumanReadableOrientationsResource } from './types/HumanReadableOrientationsResource.type';
import { Orientation } from './types/Orientation.enum';
import { AutoRotation } from './types/AutoRotation.enum';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockableOrientation } from './types/LockableOrientation.type';
import type { LockedEvent } from './types/LockedEvent.interface';
import type { HumanReadableAutoRotationsResource } from './types/HumanReadableAutoRotationsResource.type';
import EventEmitter from './EventEmitter';

class RNOrientationDirector {
  private static _humanReadableOrientationsResource: HumanReadableOrientationsResource =
    {
      [Orientation.unknown]: 'Unknown',
      [Orientation.portrait]: 'Portrait',
      [Orientation.portraitUpsideDown]: 'Portrait Upside Down',
      [Orientation.landscapeLeft]: 'Landscape Left',
      [Orientation.landscapeRight]: 'Landscape Right',
      [Orientation.faceUp]: 'Face Up',
      [Orientation.faceDown]: 'Face Down',
    };

  private static _humanReadableAutoRotationsResource: HumanReadableAutoRotationsResource =
    {
      [AutoRotation.unknown]: 'Unknown',
      [AutoRotation.enabled]: 'Enabled',
      [AutoRotation.disabled]: 'Disabled',
    };

  setHumanReadableOrientations(resource: HumanReadableOrientationsResource) {
    RNOrientationDirector._humanReadableOrientationsResource = resource;
  }

  setHumanReadableAutoRotations(resource: HumanReadableAutoRotationsResource) {
    RNOrientationDirector._humanReadableAutoRotationsResource = resource;
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

  static isAutoRotationEnabled() {
    if (Platform.OS !== 'android') {
      return AutoRotation.unknown;
    }
    return Module.isAutoRotationEnabled()
      ? AutoRotation.enabled
      : AutoRotation.disabled;
  }

  static resetSupportedInterfaceOrientations() {
    Module.resetSupportedInterfaceOrientations();
  }

  static listenForDeviceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    return EventEmitter.addDeviceOrientationDidChangeListener(callback);
  }

  static listenForInterfaceOrientationChanges(
    callback: (orientation: OrientationEvent) => void
  ) {
    return EventEmitter.addInterfaceOrientationDidChangeListener(callback);
  }

  static listenForLockChanges(callback: (event: LockedEvent) => void) {
    return EventEmitter.addLockDidChangeListener(callback);
  }

  static convertOrientationToHumanReadableString(orientation: Orientation) {
    return RNOrientationDirector._humanReadableOrientationsResource[
      orientation
    ];
  }

  static convertAutoRotationToHumanReadableString(autoRotation: AutoRotation) {
    return RNOrientationDirector._humanReadableAutoRotationsResource[
      autoRotation
    ];
  }

  /**
   * This method checks if the given orientation is lockable
   * by interface perspective.
   *
   * All orientations are lockable except for unknown, faceUp
   * and faceDown.
   *
   * This method is useful when you want to lock the interface
   * orientation from a given device orientation.
   *
   * Example: with listenForDeviceOrientationChanges
   *
   * @param orientation any orientation enum value
   * @returns true if the orientation is lockable
   */
  static isLockableOrientation(
    orientation: Orientation
  ): orientation is LockableOrientation {
    return !(
      orientation === Orientation.unknown ||
      orientation === Orientation.faceUp ||
      orientation === Orientation.faceDown
    );
  }
}

export default RNOrientationDirector;
