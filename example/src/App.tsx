import * as React from 'react';

import { Button, StyleSheet, Text, useColorScheme, View } from 'react-native';
import RNOrientationHandler, {
  Orientation,
  useDeviceOrientation,
  useInterfaceOrientation,
} from 'react-native-orientation-handler';

export default function App() {
  const isDark = useColorScheme() === 'dark';

  const interfaceOrientation = useInterfaceOrientation();
  const deviceOrientation = useDeviceOrientation();

  const textStyle = { color: isDark ? 'white' : 'black' };

  return (
    <View style={styles.container}>
      <Text style={[textStyle, styles.marginBottom]}>
        Current Interface Orientation:
        {RNOrientationHandler.convertOrientationToHumanReadableStrings(
          interfaceOrientation
        )}
      </Text>
      <Text style={[textStyle, styles.marginBottom]}>
        Current Device Orientation:
        {RNOrientationHandler.convertOrientationToHumanReadableStrings(
          deviceOrientation
        )}
      </Text>
      <Button
        title={'Log Interface Orientation'}
        onPress={() => {
          RNOrientationHandler.getInterfaceOrientation().then((orientation) => {
            console.log('Current Interface Orientation:', orientation);
          });
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Log Device Orientation'}
        onPress={() => {
          RNOrientationHandler.getDeviceOrientation().then((orientation) => {
            console.log('Current Device Orientation:', orientation);
          });
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Portrait'}
        onPress={() => {
          RNOrientationHandler.lockTo(Orientation.portrait);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Portrait Upside Down'}
        onPress={() => {
          RNOrientationHandler.lockTo(Orientation.portraitUpsideDown);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Left'}
        onPress={() => {
          RNOrientationHandler.lockTo(Orientation.landscapeLeft);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Right'}
        onPress={() => {
          RNOrientationHandler.lockTo(Orientation.landscapeRight);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Unlock'}
        onPress={() => {
          RNOrientationHandler.unlock();
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
