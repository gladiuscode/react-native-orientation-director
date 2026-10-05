import React from 'react';
import { Dimensions } from 'react-native';
import RNOrientationDirector from '../RNOrientationDirector';
import type { DisplayChangedEvent } from '../types/DisplayChangedEvent.interface';

/**
 * Hook that returns the size of the display the app is on.
 * It starts from the current screen size and listens for display changes
 * (e.g. fold / unfold on foldable devices), updating the state accordingly.
 *
 * The size is the one reported when the display last changed: rotations
 * and window resizes do not update it.
 */
const useDisplay = () => {
  const [display, setDisplay] = React.useState<DisplayChangedEvent>(() => {
    const { width, height } = Dimensions.get('screen');
    return { width, height };
  });

  React.useEffect(() => {
    const onChange = (event: DisplayChangedEvent) => {
      setDisplay({ width: event.width, height: event.height });
    };

    const subscription =
      RNOrientationDirector.listenForDisplayChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return display;
};

export default useDisplay;
