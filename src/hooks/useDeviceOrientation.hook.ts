import React from 'react';
import RNOrientationHandler from '../RNOrientationHandler';
import type { OrientationEvent } from '../types/OrientationEvent.interface';

/**
 * A custom hook to get the device orientation
 * By default, it returns `DeviceOrientation.unknown` on iOS
 */
const useDeviceOrientation = () => {
  const [orientation, setOrientation] = React.useState<number>(0);

  React.useEffect(() => {
    const onChange = (event: OrientationEvent) => {
      setOrientation(event.orientation);
    };

    return RNOrientationHandler.listenForDeviceOrientationChanges(onChange);
  }, []);

  return orientation;
};

export default useDeviceOrientation;
