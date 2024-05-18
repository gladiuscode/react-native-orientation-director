import * as React from 'react';

import { Button, StyleSheet, Text, View } from 'react-native';
import { InterfaceOrientation } from '../../src/types/InterfaceOrientation.enum';
import RNOrientationHandler from 'react-native-orientation-handler';

export default function App() {
  const [interfaceOrientation, setInterfaceOrientation] =
    React.useState<InterfaceOrientation>(InterfaceOrientation.unknown);

  React.useEffect(() => {
    RNOrientationHandler.getInterfaceOrientation().then(
      setInterfaceOrientation
    );
  }, []);

  return (
    <View style={styles.container}>
      <Text style={styles.interfaceOrientation}>
        Current Interface Orientation: {interfaceOrientation}
      </Text>
      <Button
        title={'Lock To Portrait'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.portrait);
        }}
      />
      <Button
        title={'Lock To Portrait Upside Down'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.portraitUpsideDown);
        }}
      />
      <Button
        title={'Lock To Landscape Left'}
        onPress={() => {
          RNOrientationHandler.lockTo(InterfaceOrientation.landscapeLeft);
        }}
      />
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
  interfaceOrientation: {
    marginBottom: 12,
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
