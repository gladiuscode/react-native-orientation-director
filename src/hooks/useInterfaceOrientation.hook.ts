import React, { useRef } from 'react';
import RNOrientationDirector from '../RNOrientationDirector';
import type { OrientationEvent } from '../types/OrientationEvent.interface';
import { Orientation } from '../types/Orientation.enum';

/**
 * Hook that returns the current interface orientation.
 * It listens for orientation changes and updates the state accordingly.
 */
const useInterfaceOrientation = () => {
  const initialRender = useRef(false);
  const [orientation, setOrientation] = React.useState<Orientation>(
    Orientation.unknown
  );

  React.useEffect(() => {
    if (initialRender.current) {
      return;
    }

    initialRender.current = true;
    RNOrientationDirector.getInterfaceOrientation().then(setOrientation);
  }, []);

  React.useEffect(() => {
    const onChange = (event: OrientationEvent) => {
      setOrientation(event.orientation);
    };

    const subscription =
      RNOrientationDirector.listenForInterfaceOrientationChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return orientation;
};

export default useInterfaceOrientation;
