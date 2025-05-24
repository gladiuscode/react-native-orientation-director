//
//  OrientationDirectorUtils.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

class Utils {

    private static let TAG = "Utils"

    // TODO: Add .unknown
    public func convertToOrientationFrom(uiInterfaceOrientation: UIInterfaceOrientation) -> Orientation {
        switch uiInterfaceOrientation {
        case .landscapeRight:
            return .LANDSCAPE_RIGHT
        case .portraitUpsideDown:
            return .PORTRAIT_UPSIDE_DOWN
        case .landscapeLeft:
            return .LANDSCAPE_LEFT
        default:
            return .PORTRAIT
        }
    }

    public func convertToOrientationFrom(deviceOrientation: UIDeviceOrientation) -> Orientation {
        switch deviceOrientation {
        case .landscapeRight:
            return .LANDSCAPE_RIGHT
        case .portraitUpsideDown:
            return .PORTRAIT_UPSIDE_DOWN
        case .landscapeLeft:
            return .LANDSCAPE_LEFT
        case .faceUp:
            return .FACE_UP
        case .faceDown:
            return .FACE_DOWN
        default:
            return .PORTRAIT
        }
    }

    public func convertToOrientationFrom(jsValue: NSNumber) -> Orientation {
        switch jsValue {
        case 2:
            return .LANDSCAPE_RIGHT
        case 3:
            return .PORTRAIT_UPSIDE_DOWN
        case 4:
            return .LANDSCAPE_LEFT
        case 5:
            return .LANDSCAPE
        default:
            return .PORTRAIT
        }
    }

    /**
     Note: .portraitUpsideDown only works for devices with home button and iPads
     https://developer.apple.com/documentation/uikit/uiviewcontroller/1621435-supportedinterfaceorientations
     */
    public func convertToMaskFrom(jsOrientation: Orientation) -> UIInterfaceOrientationMask {
        switch jsOrientation {
        case .PORTRAIT:
            return .portrait
        case .LANDSCAPE_RIGHT:
            return .landscapeRight
        case .PORTRAIT_UPSIDE_DOWN:
            return .portraitUpsideDown
        case .LANDSCAPE_LEFT:
            return .landscapeLeft
        case .LANDSCAPE:
          return .landscape
        default:
            return .all
        }
    }

    public func convertToInterfaceOrientationFrom(mask: UIInterfaceOrientationMask) -> UIInterfaceOrientation {
      switch mask {
        case .portrait:
          return .portrait
        case .landscapeRight:
          return .landscapeRight
        case .portraitUpsideDown:
          return .portraitUpsideDown
        case .landscapeLeft:
          return .landscapeLeft
        default:
          return .unknown
      }
    }

    public func getInterfaceOrientation() -> UIInterfaceOrientation {
        guard let windowScene = self.getCurrentWindow()?.windowScene else {
            return UIInterfaceOrientation.unknown
        }

        return windowScene.interfaceOrientation
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

}
