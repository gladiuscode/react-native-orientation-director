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

    /* This function is needed to get the current available window.
     Here in React Native we should have only one window tho.
     https://stackoverflow.com/questions/57134259/how-to-resolve-keywindow-was-deprecated-in-ios-13-0/58031897#58031897
     */
    private static func getCurrentWindow() -> UIWindow? {
        return UIApplication
            .shared
            .connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .flatMap { $0.windows }
            .last { $0.isKeyWindow }
    }

}
