import React, { useRef } from 'react';
import RNOrientationHandler from '../RNOrientationHandler';
import type { InterfaceOrientationChangesEvent } from '../types/InterfaceOrientationChangesEvent.interface';
import { InterfaceOrientation } from 'react-native-orientation-handler';

/**
 * A custom hook to get the device orientation
 * By default, it returns `DeviceOrientation.unknown` on iOS
 */
const useInterfaceOrientation = () => {
  const initialRender = useRef(false);
  const [orientation, setOrientation] = React.useState<InterfaceOrientation>(0);

  console.log('useInterfaceOrientation - orientation: ', orientation);

  React.useEffect(() => {
    if (initialRender.current) {
      return;
    }

    initialRender.current = true;
    RNOrientationHandler.getInterfaceOrientation().then(setOrientation);
  }, []);

  React.useEffect(() => {
    const onChange = (event: InterfaceOrientationChangesEvent) => {
      setOrientation(event.orientation);
    };

    return RNOrientationHandler.listenForInterfaceOrientationChanges(onChange);
  }, []);

  return orientation;
};

export default useInterfaceOrientation;
