import { Platform, type EventSubscription } from 'react-native';
import Module from './module';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockedEvent } from './types/LockedEvent.interface';

class EventEmitter {
  private static androidListenerCount = 0;

  static addDeviceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    let listener = Module.onDeviceOrientationChanged(callback);

    if (Platform.OS !== 'android') {
      return listener;
    }

    EventEmitter.androidListenerCount++;
    if (EventEmitter.androidListenerCount === 1) {
      Module.enableOrientationSensors();
    }

    return EventEmitter.createDeviceOrientationListenerProxy(listener);
  }

  static addInterfaceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    return Module.onInterfaceOrientationChanged(callback);
  }

  static addLockDidChangeListener(callback: (event: LockedEvent) => void) {
    return Module.onLockChanged(callback);
  }

  private static createDeviceOrientationListenerProxy(
    listener: EventSubscription
  ) {
    const handler: ProxyHandler<EventSubscription> = {
      get(target, propertyKey, receiver) {
        if (propertyKey === 'remove') {
          disableOrientationSensorsIfLastListener();
        }
        return Reflect.get(target, propertyKey, receiver);
      },
    };

    return new Proxy(listener, handler);

    function disableOrientationSensorsIfLastListener() {
      if (EventEmitter.androidListenerCount === 1) {
        EventEmitter.androidListenerCount = 0;
        Module.disableOrientationSensors();
        return;
      }

      if (EventEmitter.androidListenerCount === 0) {
        return;
      }

      EventEmitter.androidListenerCount--;
      return;
    }
  }
}

export default EventEmitter;
