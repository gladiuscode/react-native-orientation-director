import React from 'react';
import type { DeviceOrientationChangesEvent } from '../types/DeviceOrientationChangesEvent.interface';
import RNOrientationHandler from '../RNOrientationHandler';

/**
 * A custom hook to get the device orientation
 * By default, it returns `DeviceOrientation.unknown` on iOS
 */
const useDeviceOrientation = () => {
  const [orientation, setOrientation] = React.useState<number>(0);

  React.useEffect(() => {
    const onChange = (event: DeviceOrientationChangesEvent) => {
      setOrientation(event.orientation);
    };

    return RNOrientationHandler.listenForDeviceOrientationChanges(onChange);
  }, []);

  return orientation;
};

export default useDeviceOrientation;
