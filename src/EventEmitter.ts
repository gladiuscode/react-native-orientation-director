import { type EventSubscription, Platform } from 'react-native';
import NativeOrientationDirector from './NativeOrientationDirector';
import type { OrientationEvent } from './types/OrientationEvent.interface';
import type { LockedEvent } from './types/LockedEvent.interface';

class EventEmitter {
  private static androidListenerCount = 0;

  static addDeviceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    let listener =
      NativeOrientationDirector.onDeviceOrientationChanged(callback);

    if (Platform.OS !== 'android') {
      return listener;
    }

    EventEmitter.androidListenerCount++;
    if (EventEmitter.androidListenerCount === 1) {
      NativeOrientationDirector.enableOrientationSensors();
    }

    return EventEmitter.createDeviceOrientationListenerProxy(listener);
  }

  static addInterfaceOrientationDidChangeListener(
    callback: (orientation: OrientationEvent) => void
  ) {
    return NativeOrientationDirector.onInterfaceOrientationChanged(callback);
  }

  static addLockDidChangeListener(callback: (event: LockedEvent) => void) {
    return NativeOrientationDirector.onLockChanged(callback);
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
        NativeOrientationDirector.disableOrientationSensors();
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
