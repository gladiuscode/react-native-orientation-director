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
    private let sensorListener: OrientationSensorListener
    private let eventManager: OrientationEventManager
    private var isLocked: Bool = false
    
    @objc public var supportedInterfaceOrientation: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all

    @objc public override init() {
        eventManager = OrientationEventManager()
        sensorListener = OrientationSensorListener()
        super.init()
        sensorListener.setOnOrientationChangedCallback(callback: self.onOrientationChanged)
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////
    ///         EVENT EMITTER SETUP
    @objc public func setEventManagerDelegate(delegate: OrientationEventEmitterDelegate) {
        self.eventManager.delegate = delegate
    }
    ///
    //////////////////////////////////////////////////////////////////////////////////////////
    
    @objc public func getInterfaceOrientation() -> UIInterfaceOrientation {
        return OrientationHandlerUtils.getInterfaceOrientation()
    }
    
    @objc public func lockTo(jsOrientation: NSNumber) {
        let mask = OrientationHandlerUtils.getMaskFrom(jsOrientation: jsOrientation)
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
        
        eventManager.sendInterfaceOrientationDidChange(orientationValue: Int(truncating: jsOrientation))
        isLocked = true
    }
    
    public func onOrientationChanged(deviceOrientation: UIDeviceOrientation) {
        let jsOrientation = OrientationHandlerUtils.getJsOrientationFrom(deviceOrientation: deviceOrientation)
        self.eventManager.sendDeviceOrientationDidChange(orientationValue: jsOrientation)
    }

}
