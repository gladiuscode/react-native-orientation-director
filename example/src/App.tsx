import * as React from 'react';

import { Button, StyleSheet, Text, useColorScheme, View } from 'react-native';
import { InterfaceOrientation } from '../../src/types/InterfaceOrientation.enum';
import RNOrientationHandler, {
  useDeviceOrientation,
} from 'react-native-orientation-handler';

export default function App() {
  const isDark = useColorScheme() === 'dark';
  const deviceOrientation = useDeviceOrientation();

  const [interfaceOrientation, setInterfaceOrientation] =
    React.useState<InterfaceOrientation>(InterfaceOrientation.unknown);

  const textStyle = { color: isDark ? 'white' : 'black' };

  React.useEffect(() => {
    RNOrientationHandler.getInterfaceOrientation().then(
      setInterfaceOrientation
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text style={[textStyle, styles.marginBottom]}>
        Current Interface Orientation: {interfaceOrientation}
      </Text>
      <Text style={[textStyle, styles.marginBottom]}>
        Current Device Orientation: {deviceOrientation}
      </Text>
      <Button
        title={'Lock To Portrait'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.portrait);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Portrait Upside Down'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.portraitUpsideDown);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Left'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.landscapeLeft);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Right'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.landscapeRight);
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  marginBottom: {
    marginBottom: 12,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
