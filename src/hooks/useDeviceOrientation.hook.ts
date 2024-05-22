import React, { useRef } from 'react';
import RNOrientationDirector from '../RNOrientationDirector';
import type { OrientationEvent } from '../types/OrientationEvent.interface';
import { Orientation } from '../types/Orientation.enum';

/**
 * Hook that returns the current device orientation.
 * It listens for orientation changes and updates the state accordingly.
 */
const useDeviceOrientation = () => {
  const initialRender = useRef(false);
  const [orientation, setOrientation] = React.useState<Orientation>(
    Orientation.unknown
  );

  React.useEffect(() => {
    if (initialRender.current) {
      return;
    }

    initialRender.current = true;
    RNOrientationDirector.getDeviceOrientation().then(setOrientation);
  }, []);

  React.useEffect(() => {
    const onChange = (event: OrientationEvent) => {
      setOrientation(event.orientation);
    };

    const subscription =
      RNOrientationDirector.listenForDeviceOrientationChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return orientation;
};

export default useDeviceOrientation;
