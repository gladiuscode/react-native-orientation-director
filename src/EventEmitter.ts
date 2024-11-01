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

    const listenerCount = ModuleEventEmitter.listenerCount(
      Event.DeviceOrientationDidChange
    );

    if (listenerCount === 0) {
      Module.enableOrientationSensors();
    }

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
    const handler: ProxyHandler<EmitterSubscription> = {
      get(target, propertyKey, receiver) {
        if (propertyKey === 'remove') {
          disableOrientationSensorsIfLastListener();
        }
        return Reflect.get(target, propertyKey, receiver);
      },
    };

    return new Proxy(listener, handler);

    function disableOrientationSensorsIfLastListener() {
      const listenerCount = ModuleEventEmitter.listenerCount(
        Event.DeviceOrientationDidChange
      );

      if (listenerCount === 1) {
        Module.disableOrientationSensors();
      }
    }
  }
}

export default EventEmitter;
