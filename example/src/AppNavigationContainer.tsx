import React from 'react';
import {
  NavigationContainer,
  type NavigationState,
} from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Home from './screens/Homepage';
import Explore from './screens/Explore';
import RNOrientationDirector from 'react-native-orientation-director';
import InnerExplore from './screens/InnerExplore';

function AppNavigationContainer() {
  const handleOnReady = async () => {
    console.log('App Navigation is ready');

    // With hot reload this returns UNKNOWN because
    // the listener is disabled on the native side.
    const initialDeviceOrientation =
      await RNOrientationDirector.getDeviceOrientation();
    const initialInterfaceOrientation =
      await RNOrientationDirector.getInterfaceOrientation();

    console.log('Initial device orientation: ', initialDeviceOrientation);
    console.log('Initial interface orientation: ', initialInterfaceOrientation);
  };

  const handleOnStateChange = (state?: NavigationState) => {
    console.log('App Navigation state changed');
    console.log('New state: ', state);
  };

  return (
    <NavigationContainer
      onReady={handleOnReady}
      onStateChange={handleOnStateChange}
    >
      <MainStack />
    </NavigationContainer>
  );
}

export default AppNavigationContainer;

const Stack = createNativeStackNavigator();
function MainStack() {
  return (
    <Stack.Navigator>
      <Stack.Screen name="Home" component={Home} />
      <Stack.Screen name="Explore" component={Explore} />
      <Stack.Screen name="InnerExplore" component={InnerExplore} />
    </Stack.Navigator>
  );
}
