import React, { useRef } from 'react';
import RNOrientationHandler from '../RNOrientationHandler';
import type { OrientationEvent } from '../types/OrientationEvent.interface';
import { Orientation } from 'react-native-orientation-handler';

/**
 * A custom hook to get the device orientation
 * By default, it returns `DeviceOrientation.unknown` on iOS
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

    return RNOrientationHandler.listenForInterfaceOrientationChanges(onChange);
  }, []);

  return orientation;
};

export default useInterfaceOrientation;
