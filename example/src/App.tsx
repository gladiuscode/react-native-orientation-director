import * as React from 'react';

import { StyleSheet, Text, View } from 'react-native';
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
      <Text>Current Interface Orientation: {interfaceOrientation}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
