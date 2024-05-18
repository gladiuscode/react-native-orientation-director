import InterfaceOrientation from './types/InterfaceOrientation.enum';
import Module, { EventEmitter } from './module';
import type { DeviceOrientationChangesEvent } from './types/DeviceOrientationChangesEvent.interface';
import Event from './types/Event.enum';

class RNOrientationHandler {
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
}

export default RNOrientationHandler;
