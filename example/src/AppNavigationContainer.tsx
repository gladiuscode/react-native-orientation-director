import React from 'react';
import {
  NavigationContainer,
  type NavigationState,
} from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import Home from './screens/Homepage';
import Explore from './screens/Explore';

function AppNavigationContainer() {
  const handleOnReady = () => {
    console.log('App Navigation is ready');
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
    </Stack.Navigator>
  );
}
