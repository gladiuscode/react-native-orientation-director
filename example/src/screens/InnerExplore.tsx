import { Text, View } from 'react-native';
import { innerExploreStyle } from './styles';
import RNOrientationDirector, {
  useDeviceOrientation,
} from 'react-native-orientation-director';

function InnerExplore() {
  const deviceOrientation = useDeviceOrientation();

  return (
    <View style={innerExploreStyle.container}>
      <Text>Inner Explore!</Text>
      <View style={innerExploreStyle.marginBottom} />

      <View style={innerExploreStyle.body}>
        <Text style={[innerExploreStyle.text, innerExploreStyle.marginBottom]}>
          Current Device Orientation:
          {RNOrientationDirector.convertOrientationToHumanReadableString(
            deviceOrientation
          )}
        </Text>
      </View>
    </View>
  );
}

export default InnerExplore;
