# react-native-orientation-handler

A simple library that allows you to handle orientation changes in your React Native app.
Written in Kotlin, Swift and Typescript. It supports both the Old and New React Native architecture.

## Features

- [x] Get the current orientation of the device
- [x] Get the current orientation of the interface
- [x] Listen to device orientation changes
- [x] Listen to interface orientation changes
- [x] Lock the interface orientation to a specific orientation
- [x] Unlock the interface orientation

## Installation

You can install the package via npm or yarn:

```sh
npm install react-native-orientation-handler
```
```sh
yarn add react-native-orientation-handler
```

Don't forget to run pod-install

## Setup

To properly handle interface orientation changes in iOS, you need to update your AppDelegate.mm file.
In your AppDelegate.mm file, import "OrientationHandler.h" and implement supportedInterfaceOrientationsForWindow method as follows:

```objc
#import <OrientationHandler.h>

- (UIInterfaceOrientationMask)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window
{
  return [OrientationHandler getSupportedInterfaceOrientationsForWindow];
}
```

If you need help, you can check the example project.

There is no need to do anything in Android, it works out of the box.

## Usage

This library exports a class called: [RNOrientationHandler](https://github.com/gladiuscode/react-native-orientation-handler/blob/main/src/RNOrientationHandler.ts) that exposes the following methods:

| Method                                  | Description                                                                     |
|-----------------------------------------|---------------------------------------------------------------------------------|
| getInterfaceOrientation                 | Returns the last interface orientation                                          |
| getDeviceOrientation                    | Returns the last device orientation                                             |
| lockTo                                  | Locks the interface to a specific orientation                                   |
| unlock                                  | Unlock the interface                                                            |
| listenForDeviceOrientationChanges       | Triggers a provided callback each time the device orientation changes           |
| listenForInterfaceOrientationChanges    | Triggers a provided callback each time the interface orientation changes        |
| convertOrientationToHumanReadableString | Returns a human readable string based on the given orientation                  |
| setLocalizedStringProvider              | Sets the mapping needed to convert orientation values to human readable strings |

In addition, the library exposes the following hooks:

| Hook                                                                                                                                           | Description                                                      |
|------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------|
| [useInterfaceOrientation](https://github.com/gladiuscode/react-native-orientation-handler/blob/main/src/hooks/useInterfaceOrientation.hook.ts) | Returns the current interface orientation and listens to changes |
| [useDeviceOrientation](https://github.com/gladiuscode/react-native-orientation-handler/blob/main/src/hooks/useDeviceOrientation.hook.ts)       | Returns the current device orientation and listens to changes    |

Head over to the [example project](example) to see how to use the library.

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
