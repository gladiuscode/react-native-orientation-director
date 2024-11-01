import React from 'react';
import { Text, View } from 'react-native';
import { exploreStyle } from './styles';
import RNOrientationDirector, {
  useDeviceOrientation,
  useInterfaceOrientation,
  useIsInterfaceOrientationLocked,
} from 'react-native-orientation-director';

function Explore() {
  const interfaceOrientation = useInterfaceOrientation();
  const deviceOrientation = useDeviceOrientation();
  const isInterfaceOrientationLocked = useIsInterfaceOrientationLocked();

  return (
    <View style={exploreStyle.container}>
      <Text>Explore!</Text>

      <View style={exploreStyle.body}>
        <Text style={[exploreStyle.text, exploreStyle.marginBottom]}>
          Current Interface Orientation:
          {RNOrientationDirector.convertOrientationToHumanReadableString(
            interfaceOrientation
          )}
        </Text>
        <Text style={[exploreStyle.text, exploreStyle.marginBottom]}>
          Current Device Orientation:
          {RNOrientationDirector.convertOrientationToHumanReadableString(
            deviceOrientation
          )}
        </Text>
        <Text style={[exploreStyle.text, exploreStyle.marginBottom]}>
          Is Interface Orientation Locked:
          {isInterfaceOrientationLocked ? 'Yes' : 'No'}
        </Text>
      </View>
    </View>
  );
}

export default Explore;
