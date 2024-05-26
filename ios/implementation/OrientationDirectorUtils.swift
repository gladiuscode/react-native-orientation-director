//
//  OrientationDirectorUtils.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

class OrientationDirectorUtils {

    private static let TAG = "OrientationDirectorUtils"

    public static func getOrientationFrom(uiInterfaceOrientation: UIInterfaceOrientation) -> Orientation {
        var orientation = Orientation.UNKNOWN

        switch(uiInterfaceOrientation) {
        case UIInterfaceOrientation.landscapeRight: // Home button on the right
            orientation = Orientation.LANDSCAPE_LEFT
        case UIInterfaceOrientation.portraitUpsideDown:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case UIInterfaceOrientation.landscapeLeft: // Home button on the left
            orientation = Orientation.LANDSCAPE_RIGHT
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    public static func getOrientationFrom(deviceOrientation: UIDeviceOrientation) -> Orientation {
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

    public static func getOrientationFrom(jsOrientation: NSNumber) -> Orientation {
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
    
    public static func getOrientationFrom(mask: UIInterfaceOrientationMask) -> Orientation {
        var orientation = Orientation.UNKNOWN
        
        switch(mask) {
        case UIInterfaceOrientationMask.portraitUpsideDown:
            orientation = Orientation.PORTRAIT_UPSIDE_DOWN
        case UIInterfaceOrientationMask.landscapeRight:
            orientation = Orientation.LANDSCAPE_LEFT
        case UIInterfaceOrientationMask.landscapeLeft:
            orientation = Orientation.LANDSCAPE_RIGHT
        default:
            orientation = Orientation.PORTRAIT
        }

        return orientation
    }

    /*
        Note: .portraitUpsideDown only works for devices with home button
        //https://developer.apple.com/documentation/uikit/uiviewcontroller/1621435-supportedinterfaceorientations
     */
    public static func getMaskFrom(orientation: Orientation) -> UIInterfaceOrientationMask {
        var mask = UIInterfaceOrientationMask.portrait

        switch(orientation) {
        case Orientation.LANDSCAPE_RIGHT:
            mask = UIInterfaceOrientationMask.landscapeLeft
        case Orientation.PORTRAIT_UPSIDE_DOWN:
            mask = UIInterfaceOrientationMask.portraitUpsideDown
        case Orientation.LANDSCAPE_LEFT:
            mask = UIInterfaceOrientationMask.landscapeRight
        default:
            mask = UIInterfaceOrientationMask.portrait
        }

        return mask
    }

    public static func getInterfaceOrientation() -> UIInterfaceOrientation {
        guard let windowScene = self.getCurrentWindow()?.windowScene else {
            return UIInterfaceOrientation.unknown
        }

        return windowScene.interfaceOrientation;
    }

    /* This function is needed to get the current available window.
     Here in React Native we should have only one window tho.
     https://stackoverflow.com/questions/57134259/how-to-resolve-keywindow-was-deprecated-in-ios-13-0/58031897#58031897
     */
    public static func getCurrentWindow() -> UIWindow? {
        return UIApplication
            .shared
            .connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .last { $0.isKeyWindow }
    }
    
    public static func readSupportedInterfaceOrientationsFromBundle() -> [UIInterfaceOrientationMask] {
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
