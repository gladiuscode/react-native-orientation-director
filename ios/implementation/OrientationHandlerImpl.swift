//
//  OrientationHandlerImpl.swift
//  react-native-orientation-handler
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation
import UIKit

@objc public class OrientationHandlerImpl : NSObject {

    private static let TAG = "OrientationHandlerImpl"
    
    @objc public var supportedInterfaceOrientation: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all

    @objc public func getInterfaceOrientation() -> UIInterfaceOrientation {
        return OrientationHandlerUtils.getInterfaceOrientation()
    }
    
    @objc public func lockTo(orientation: NSNumber) {
        let mask = OrientationHandlerUtils.getMaskFromOrientation(orientation: orientation)
        self.supportedInterfaceOrientation = mask
        
        DispatchQueue.main.async {
            if #available(iOS 16.0, *) {
                guard let window = OrientationHandlerUtils.getCurrentWindow() else {
                    return
                }

                guard let rootViewController = window.rootViewController else {
                    return
                }

                guard let windowScene = window.windowScene else {
                    return
                }

                rootViewController.setNeedsUpdateOfSupportedInterfaceOrientations()

                windowScene.requestGeometryUpdate(.iOS(interfaceOrientations: mask)) { error in
                    print("\(OrientationHandlerImpl.TAG) - requestGeometryUpdate error", error)
                }
            } else {
                UIDevice.current.setValue(mask.rawValue, forKey: "orientation")
                UIViewController.attemptRotationToDeviceOrientation()
            }
        }
    }

}
