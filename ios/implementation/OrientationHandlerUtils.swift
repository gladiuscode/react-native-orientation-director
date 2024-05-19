//
//  OrientationHandlerUtils.swift
//  react-native-orientation-handler
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

class OrientationHandlerUtils {

    private static let TAG = "OrientationHandlerUtils"

    public static func getInterfaceOrientation() -> UIInterfaceOrientation {
        guard let windowScene = self.getCurrentWindow()?.windowScene else {
            return UIInterfaceOrientation.unknown
        }

        return windowScene.interfaceOrientation;
    }
    
    /*
        Note: .portraitUpsideDown only works for devices with home button
        //https://developer.apple.com/documentation/uikit/uiviewcontroller/1621435-supportedinterfaceorientations
     */
    public static func getMaskFrom(jsOrientation: NSNumber) -> UIInterfaceOrientationMask {
        var mask = UIInterfaceOrientationMask.portrait
        switch(jsOrientation) {
        case 2:
            mask = UIInterfaceOrientationMask.landscapeRight
        case 3:
            mask = UIInterfaceOrientationMask.portraitUpsideDown
        case 4:
            mask = UIInterfaceOrientationMask.landscapeLeft
        default:
            mask = UIInterfaceOrientationMask.portrait
        }
        
        return mask
    }
    
    public static func getJsOrientationFrom(deviceOrientation: UIDeviceOrientation) -> Int {
        var jsOrientation = 1
        
        switch(deviceOrientation) {
        case UIDeviceOrientation.landscapeRight:
            jsOrientation = 2
        case UIDeviceOrientation.portraitUpsideDown:
            jsOrientation = 3
        case UIDeviceOrientation.landscapeLeft:
            jsOrientation = 4
        case UIDeviceOrientation.faceUp:
            jsOrientation = 4
        default:
            jsOrientation = 1
        }
        
        return jsOrientation
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

}
