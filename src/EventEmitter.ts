import { Platform, type EmitterSubscription } from 'react-native';
import Module, { ModuleEventEmitter } from './module';
import Event from './types/Event.enum';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockedEvent } from './types/LockedEvent.interface';

class EventEmitter {
  static addDeviceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    let listener = ModuleEventEmitter.addListener(
      Event.DeviceOrientationDidChange,
      callback
    );

    if (Platform.OS !== 'android') {
      return listener;
    }

    Module.enableOrientationSensors();

    return EventEmitter.createDeviceOrientationListenerProxy(listener);
  }

  static addInterfaceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    return ModuleEventEmitter.addListener(
      Event.InterfaceOrientationDidChange,
      callback
    );
  }

  static addLockDidChangeListener(callback: (event: LockedEvent) => void) {
    return ModuleEventEmitter.addListener(Event.LockDidChange, callback);
  }

  private static createDeviceOrientationListenerProxy(
    listener: EmitterSubscription
  ) {
    return new Proxy(listener, {
      get(target, propertyKey, receiver) {
        if (propertyKey !== 'remove') {
          return Reflect.get(target, propertyKey, receiver);
        }

        const listenerCount = ModuleEventEmitter.listenerCount(
          Event.DeviceOrientationDidChange
        );

        if (listenerCount === 1) {
          Module.disableOrientationSensors();
        }

        return Reflect.get(target, propertyKey, receiver);
      },
    });
  }
}

export default EventEmitter;
