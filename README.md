![NPM Version](https://img.shields.io/npm/v/react-native-orientation-director)

---

# react-native-orientation-director

A React Native library that allows you to listen to orientation changes, lock interface orientation
to a selected one and get current orientation.
Written in Kotlin, Swift and Typescript. It supports both the Old and New React Native architecture.

This library takes inspiration from and builds upon the following amazing alternatives:

1. [react-native-orientation-locker](https://github.com/wonday/react-native-orientation-locker)
2. [react-native-orientation-handler](https://github.com/KroosX4V/react-native-orientation-manager)

## Features

- [x] Get the current orientation of the device
- [x] Get the current orientation of the interface
- [x] Get the current interface orientation status (locked or unlocked)
- [x] Listen to device orientation changes
- [x] Listen to interface orientation changes
- [x] Listen to interface orientation status changes
- [x] Lock the interface orientation to a specific orientation
- [x] Unlock the interface orientation
- [x] Reset supported interface orientations to settings
- [x] Check if autorotation is enabled (Android only)

## Installation

### React Native Bare

You can install the package via npm or yarn:

```sh
npm install react-native-orientation-director
```

```sh
yarn add react-native-orientation-director
```

Don't forget to run pod-install.

### Expo

This library can be installed only for [Development Builds](https://docs.expo.dev/develop/development-builds/introduction/)
using the following command:

```sh
npx expo install react-native-orientation-director
```

## Setup

To properly handle interface orientation changes in iOS, you need to update your AppDelegate.mm file.
In your AppDelegate.mm file, import "OrientationDirector.h" and implement supportedInterfaceOrientationsForWindow method as follows:

```objc
#import <OrientationDirector.h>

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
  return [OrientationDirector getSupportedInterfaceOrientationsForWindow];
}
```

If you need help, you can check the example project.

There is no need to do anything in Android, it works out of the box.

## Usage

This library exports a class called: [RNOrientationDirector](https://github.com/gladiuscode/react-native-orientation-director/blob/main/src/RNOrientationDirector.ts) that exposes the following methods:

| Method                                   | Description                                                                       |
| ---------------------------------------- | --------------------------------------------------------------------------------- |
| getInterfaceOrientation                  | Returns the last interface orientation                                            |
| getDeviceOrientation                     | Returns the last device orientation                                               |
| lockTo                                   | Locks the interface to a specific orientation                                     |
| unlock                                   | Unlock the interface                                                              |
| isLocked                                 | Returns the current interface orientation status (locked / unlocked)              |
| isAutoRotationEnabled                    | (Android Only) Returns if auto rotation is enabled                                |
| listenForDeviceOrientationChanges        | Triggers a provided callback each time the device orientation changes             |
| listenForInterfaceOrientationChanges     | Triggers a provided callback each time the interface orientation changes          |
| listenForLockChanges                     | Triggers a provided callback each time the interface orientation status changes   |
| convertOrientationToHumanReadableString  | Returns a human readable string based on the given orientation                    |
| convertAutoRotationToHumanReadableString | Returns a human readable string based on the given auto rotation                  |
| setHumanReadableOrientations             | Sets the mapping needed to convert orientation values to human readable strings   |
| setHumanReadableAutoRotations            | Sets the mapping needed to convert auto rotation values to human readable strings |
| resetSupportedInterfaceOrientations      | Resets the supported interface orientations to settings                           |
| isLockableOrientation                    | Determines if orientation is lockable                                             |

In addition, the library exposes the following hooks:

| Hook                                                                                                                                                            | Description                                                             |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| [useInterfaceOrientation](https://github.com/gladiuscode/react-native-orientation-director/blob/main/src/hooks/useInterfaceOrientation.hook.ts)                 | Returns the current interface orientation and listens to changes        |
| [useDeviceOrientation](https://github.com/gladiuscode/react-native-orientation-director/blob/main/src/hooks/useDeviceOrientation.hook.ts)                       | Returns the current device orientation and listens to changes           |
| [useIsInterfaceOrientationLocked](https://github.com/gladiuscode/react-native-orientation-director/blob/main/src/hooks/useIsInterfaceOrientationLocked.hook.ts) | Returns the current interface orientation status and listens to changes |

Head over to the [example project](example) to see how to use the library.

### Warning

Please be aware that there is a subtle difference between the device orientation
and the interface orientation.

When you device is either in landscape left or right orientation, your interface
is inverted, this is why lockTo method needs a second parameter to discriminate
which type of orientation your are supplying.

To match developers expectations, if you supply a device orientation and
OrientationType.device, lockTo switches landscapeRight with left and vice versa
to property align the interface orientation.

This behavior comes from the native API, you can find more information in their
documentation:

1. [iOS - UIInterfaceOrientation](https://developer.apple.com/documentation/uikit/uiinterfaceorientation)
2. [iOS - UIDeviceOrientation](https://developer.apple.com/documentation/uikit/uideviceorientation)
3. [Android - getRotation](<https://developer.android.com/reference/android/view/Display#getRotation()>)

### Android

Since on Android we need to deal with sensors and their usage, it is worth noting that the device orientation computation works
differently than on iOS, mainly in the following ways:

1. Upon start up, all required sensors are enabled just for the initial device orientation computation, then they are disabled;
2. Each time a new device orientation listener is added, all required sensors are enabled if disabled;
3. After the last device orientation listener is removed, all required sensors are disabled;

This behavior allows us to follow Google's best practices related to the Sensors Framework. More [here](https://developer.android.com/develop/sensors-and-location/sensors/sensors_overview#sensors-practices).

## Roadmap

- [ ] Add JS side tests
- [ ] Add iOS side tests

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
