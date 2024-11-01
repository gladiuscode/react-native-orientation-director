import React from 'react';
import { Button, Text, View } from 'react-native';
import { exploreStyle } from './styles';
import RNOrientationDirector, {
  useDeviceOrientation,
  useInterfaceOrientation,
  useIsInterfaceOrientationLocked,
} from 'react-native-orientation-director';
import { useNavigation } from '@react-navigation/native';

function Explore() {
  const navigation = useNavigation();

  const interfaceOrientation = useInterfaceOrientation();
  const deviceOrientation = useDeviceOrientation();
  const isInterfaceOrientationLocked = useIsInterfaceOrientationLocked();

  const handleGoToInnerExploreOnPress = () => {
    navigation.navigate('InnerExplore' as never);
  };

  return (
    <View style={exploreStyle.container}>
      <Text>Explore!</Text>
      <Button
        title="Go to inner explore"
        onPress={handleGoToInnerExploreOnPress}
      />

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
