import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { Button, Text, View } from 'react-native';

function Home() {
  const navigation = useNavigation();

  const handleGoToExploreOnPress = () => {
    navigation.navigate('Explore');
  };

  return (
    <View>
      <Text>Welcome!</Text>
      <Button title="Go to explore" onPress={handleGoToExploreOnPress} />
    </View>
  );
}

export default Home;
