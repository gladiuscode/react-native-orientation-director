//
//  OrientationDirectorUtils.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

class OrientationDirectorUtils {

    private static let TAG = "OrientationDirectorUtils"

    public func getOrientationFrom(uiInterfaceOrientation: UIInterfaceOrientation) -> Orientation {
        var orientation = Orientation.UNKNOWN

        switch(uiInterfaceOrientation) {
        case UIInterfaceOrientation.landscapeRight:
            orientation = Orientation.LANDSCAPE_RIGHT
        case UIInterfaceOrientation.portraitUpsideDown:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case UIInterfaceOrientation.landscapeLeft:
            orientation = Orientation.LANDSCAPE_LEFT
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    public func getOrientationFrom(deviceOrientation: UIDeviceOrientation) -> Orientation {
        var orientation = Orientation.UNKNOWN

        switch(deviceOrientation) {
        case UIDeviceOrientation.landscapeRight:
            orientation = Orientation.LANDSCAPE_RIGHT
        case UIDeviceOrientation.portraitUpsideDown:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case UIDeviceOrientation.landscapeLeft:
            orientation = Orientation.LANDSCAPE_LEFT
        case UIDeviceOrientation.faceUp:
            orientation = Orientation.FACE_UP
        case UIDeviceOrientation.faceDown:
            orientation = Orientation.FACE_DOWN
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    public func getOrientationFrom(jsOrientation: NSNumber) -> Orientation {
        var orientation = Orientation.UNKNOWN

        switch(jsOrientation) {
        case 2:
            orientation = Orientation.LANDSCAPE_RIGHT
        case 3:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case 4:
            orientation = Orientation.LANDSCAPE_LEFT
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    public func getOrientationFrom(mask: UIInterfaceOrientationMask) -> Orientation {
        var orientation = Orientation.UNKNOWN

        switch(mask) {
        case UIInterfaceOrientationMask.portraitUpsideDown:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case UIInterfaceOrientationMask.landscapeRight:
            orientation = Orientation.LANDSCAPE_RIGHT
        case UIInterfaceOrientationMask.landscapeLeft:
            orientation = Orientation.LANDSCAPE_LEFT
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    /**
     Note: .portraitUpsideDown only works for devices with home button and iPads
     https://developer.apple.com/documentation/uikit/uiviewcontroller/1621435-supportedinterfaceorientations
     */
    public func getMaskFrom(jsOrientation: Orientation) -> UIInterfaceOrientationMask {
        switch(jsOrientation) {
        case Orientation.PORTRAIT:
            return UIInterfaceOrientationMask.portrait
        case Orientation.LANDSCAPE_RIGHT:
            return UIInterfaceOrientationMask.landscapeRight
        case Orientation.PORTRAIT_UPSIDE_DOWN:
            return UIInterfaceOrientationMask.portraitUpsideDown
        case Orientation.LANDSCAPE_LEFT:
            return UIInterfaceOrientationMask.landscapeLeft
        default:
            return UIInterfaceOrientationMask.all
        }
    }

    /**
     Note: .portraitUpsideDown only works for devices with home button and iPads
     https://developer.apple.com/documentation/uikit/uiviewcontroller/1621435-supportedinterfaceorientations
     */
    public func getMaskFrom(deviceOrientation: Orientation) -> UIInterfaceOrientationMask {
        switch(deviceOrientation) {
        case Orientation.PORTRAIT:
            return UIInterfaceOrientationMask.portrait
        case Orientation.LANDSCAPE_RIGHT:
            return UIInterfaceOrientationMask.landscapeLeft
        case Orientation.PORTRAIT_UPSIDE_DOWN:
            return UIInterfaceOrientationMask.portraitUpsideDown
        case Orientation.LANDSCAPE_LEFT:
            return UIInterfaceOrientationMask.landscapeRight
        default:
            return UIInterfaceOrientationMask.all
        }
    }

    public func getInterfaceOrientation() -> UIInterfaceOrientation {
        guard let windowScene = self.getCurrentWindow()?.windowScene else {
            return UIInterfaceOrientation.unknown
        }

        return windowScene.interfaceOrientation;
    }

    /* This function is needed to get the current available window.
     Here in React Native we should have only one window tho.
     https://stackoverflow.com/questions/57134259/how-to-resolve-keywindow-was-deprecated-in-ios-13-0/58031897#58031897
     */
    public func getCurrentWindow() -> UIWindow? {
        return UIApplication
            .shared
            .connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .last { $0.isKeyWindow }
    }

    public func readSupportedInterfaceOrientationsFromBundle() -> [UIInterfaceOrientationMask] {
        guard let rawOrientations = Bundle.main.object(forInfoDictionaryKey: "UISupportedInterfaceOrientations") as? [String] else {
            return [UIInterfaceOrientationMask.all]
        }

        return rawOrientations.compactMap { orientation in
            switch orientation {
            case "UIInterfaceOrientationPortrait":
                return UIInterfaceOrientationMask.portrait
            case "UIInterfaceOrientationLandscapeLeft":
                return UIInterfaceOrientationMask.landscapeLeft
            case "UIInterfaceOrientationLandscapeRight":
                return UIInterfaceOrientationMask.landscapeRight
            case "UIInterfaceOrientationPortraitUpsideDown":
                return UIInterfaceOrientationMask.portraitUpsideDown
            default:
                return UIInterfaceOrientationMask.allButUpsideDown
            }
        }
    }

}
