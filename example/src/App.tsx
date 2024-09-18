import * as React from 'react';

import { Button, ScrollView, StyleSheet, Text, View } from 'react-native';
import RNOrientationDirector, {
  Orientation,
  useDeviceOrientation,
  useInterfaceOrientation,
  useIsInterfaceOrientationLocked,
} from 'react-native-orientation-director';

export default function App() {
  const interfaceOrientation = useInterfaceOrientation();
  const deviceOrientation = useDeviceOrientation();
  const isInterfaceOrientationLocked = useIsInterfaceOrientationLocked();

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.contentContainerStyle}
    >
      <Text style={[styles.text, styles.marginBottom]}>
        Current Interface Orientation:
        {RNOrientationDirector.convertOrientationToHumanReadableString(
          interfaceOrientation
        )}
      </Text>
      <Text style={[styles.text, styles.marginBottom]}>
        Current Device Orientation:
        {RNOrientationDirector.convertOrientationToHumanReadableString(
          deviceOrientation
        )}
      </Text>
      <Text style={[styles.text, styles.marginBottom]}>
        Is Interface Orientation Locked:
        {isInterfaceOrientationLocked ? 'Yes' : 'No'}
      </Text>
      <Button
        title={'Log Interface Orientation'}
        onPress={() => {
          RNOrientationDirector.getInterfaceOrientation().then(
            (orientation) => {
              console.log(
                'Current Interface Orientation:',
                RNOrientationDirector.convertOrientationToHumanReadableString(
                  orientation
                )
              );
            }
          );
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Log Device Orientation'}
        onPress={() => {
          RNOrientationDirector.getDeviceOrientation().then((orientation) => {
            console.log(
              'Current Device Orientation:',
              RNOrientationDirector.convertOrientationToHumanReadableString(
                orientation
              )
            );
          });
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Log is Interface Orientation Locked'}
        onPress={() => {
          console.log('isLocked: ', RNOrientationDirector.isLocked());
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Log is Auto Rotation Enabled'}
        onPress={() => {
          const isAutoRotationEnabled =
            RNOrientationDirector.isAutoRotationEnabled();
          const humanReadableAutoRotation =
            RNOrientationDirector.convertAutoRotationToHumanReadableString(
              isAutoRotationEnabled
            );
          console.log('isAutoRotationEnabled: ', humanReadableAutoRotation);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Portrait'}
        onPress={() => {
          RNOrientationDirector.lockTo(Orientation.portrait);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Portrait Upside Down'}
        onPress={() => {
          RNOrientationDirector.lockTo(Orientation.portraitUpsideDown);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Left'}
        onPress={() => {
          RNOrientationDirector.lockTo(Orientation.landscapeLeft);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Lock To Landscape Right'}
        onPress={() => {
          RNOrientationDirector.lockTo(Orientation.landscapeRight);
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Unlock'}
        onPress={() => {
          RNOrientationDirector.unlock();
        }}
      />
      <View style={styles.marginBottom} />
      <Button
        title={'Reset'}
        onPress={() => {
          RNOrientationDirector.resetSupportedInterfaceOrientations();
        }}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  contentContainerStyle: {
    alignItems: 'center',
    justifyContent: 'center',
    flexGrow: 1,
  },
  text: {
    color: 'black',
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
