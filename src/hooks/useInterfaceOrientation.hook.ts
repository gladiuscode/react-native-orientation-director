import React, { useRef } from 'react';
import RNOrientationHandler from '../RNOrientationHandler';
import type { OrientationEvent } from '../types/OrientationEvent.interface';
import { Orientation } from 'react-native-orientation-handler';

/**
 * Hook that returns the current interface orientation.
 * It listens for orientation changes and updates the state accordingly.
 */
const useInterfaceOrientation = () => {
  const initialRender = useRef(false);
  const [orientation, setOrientation] = React.useState<Orientation>(0);

  React.useEffect(() => {
    if (initialRender.current) {
      return;
    }

    initialRender.current = true;
    RNOrientationHandler.getInterfaceOrientation().then(setOrientation);
  }, []);

  React.useEffect(() => {
    const onChange = (event: OrientationEvent) => {
      setOrientation(event.orientation);
    };

    const subscription =
      RNOrientationHandler.listenForInterfaceOrientationChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return orientation;
};

export default useInterfaceOrientation;
