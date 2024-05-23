import React from 'react';
import RNOrientationDirector from '../RNOrientationDirector';
import type { LockedEvent } from '../types/LockedEvent.interface';

/**
 * Hook that returns whether the interface is locked.
 * It listens for changes and updates the state accordingly.
 */
const useIsInterfaceOrientationLocked = () => {
  const [orientation, setOrientation] = React.useState<boolean>(() =>
    RNOrientationDirector.isLocked()
  );

  React.useEffect(() => {
    const onChange = (event: LockedEvent) => {
      setOrientation(event.locked);
    };

    const subscription = RNOrientationDirector.listenForLockChanges(onChange);
    return () => {
      subscription.remove();
    };
  }, []);

  return orientation;
};

export default useIsInterfaceOrientationLocked;
