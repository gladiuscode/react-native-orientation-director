import { useNavigation } from '@react-navigation/native';
import React from 'react';
import { Button, ScrollView, Text, View } from 'react-native';
import { homepageStyle } from './styles';
import RNOrientationDirector, {
  Orientation,
} from 'react-native-orientation-director';

function Home() {
  const navigation = useNavigation();

  const handleGoToExploreOnPress = () => {
    navigation.navigate('Explore' as never);
  };

  return (
    <ScrollView>
      <Text>Welcome!</Text>
      <View style={homepageStyle.marginBottom} />
      <Button title="Go to explore" onPress={handleGoToExploreOnPress} />

      <View style={homepageStyle.buttonsContainer}>
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
        <View style={homepageStyle.marginBottom} />
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
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Log is Interface Orientation Locked'}
          onPress={() => {
            console.log('isLocked: ', RNOrientationDirector.isLocked());
          }}
        />
        <View style={homepageStyle.marginBottom} />
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
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Lock To Portrait'}
          onPress={() => {
            RNOrientationDirector.lockTo(Orientation.portrait);
          }}
        />
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Lock To Portrait Upside Down'}
          onPress={() => {
            RNOrientationDirector.lockTo(Orientation.portraitUpsideDown);
          }}
        />
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Lock To Landscape Left'}
          onPress={() => {
            RNOrientationDirector.lockTo(Orientation.landscapeLeft);
          }}
        />
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Lock To Landscape Right'}
          onPress={() => {
            RNOrientationDirector.lockTo(Orientation.landscapeRight);
          }}
        />
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Unlock'}
          onPress={() => {
            RNOrientationDirector.unlock();
          }}
        />
        <View style={homepageStyle.marginBottom} />
        <Button
          title={'Reset'}
          onPress={() => {
            RNOrientationDirector.resetSupportedInterfaceOrientations();
          }}
        />
      </View>
    </ScrollView>
  );
}

export default Home;
