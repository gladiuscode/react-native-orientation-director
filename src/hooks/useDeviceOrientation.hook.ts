import React, { useRef } from 'react';
import RNOrientationHandler from '../RNOrientationHandler';
import type { OrientationEvent } from '../types/OrientationEvent.interface';

/**
 * Hook that returns the current device orientation.
 * It listens for orientation changes and updates the state accordingly.
 */
const useDeviceOrientation = () => {
  const initialRender = useRef(false);
  const [orientation, setOrientation] = React.useState<number>(0);

  React.useEffect(() => {
    if (initialRender.current) {
      return;
    }

    initialRender.current = true;
    RNOrientationHandler.getDeviceOrientation().then(setOrientation);
  }, []);

  React.useEffect(() => {
    const onChange = (event: OrientationEvent) => {
      setOrientation(event.orientation);
    };

    const subscription =
      RNOrientationHandler.listenForDeviceOrientationChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return orientation;
};

export default useDeviceOrientation;
