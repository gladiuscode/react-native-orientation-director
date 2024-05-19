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
    private var lastInterfaceOrientation: Orientation = Orientation.UNKNOWN

    @objc public var supportedInterfaceOrientation: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all

    @objc public override init() {
        eventManager = OrientationEventManager()
        sensorListener = OrientationSensorListener()
        super.init()
        sensorListener.setOnOrientationChangedCallback(callback: self.onOrientationChanged)
        lastInterfaceOrientation = getInterfaceOrientation()
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///         EVENT EMITTER SETUP
    @objc public func setEventManagerDelegate(delegate: OrientationEventEmitterDelegate) {
        self.eventManager.delegate = delegate
    }
    ///
    //////////////////////////////////////////////////////////////////////////////////////////

    @objc public func getInterfaceOrientation() -> Orientation {
        if (isLocked) {
            return lastInterfaceOrientation
        }

        let interfaceOrientation = OrientationHandlerUtils.getInterfaceOrientation()
        return OrientationHandlerUtils.getOrientationFrom(uiInterfaceOrientation: interfaceOrientation)
    }
    
    @objc public func getDeviceOrientation() -> Orientation {
        return OrientationHandlerUtils.getOrientationFrom(deviceOrientation: UIDevice.current.orientation)
    }

    @objc public func lockTo(jsOrientation: NSNumber) {
        let orientation = OrientationHandlerUtils.getOrientationFrom(jsOrientation: jsOrientation)
        let mask = OrientationHandlerUtils.getMaskFrom(orientation: orientation)

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

        eventManager.sendInterfaceOrientationDidChange(orientationValue: orientation.rawValue)
        lastInterfaceOrientation = orientation
        isLocked = true
    }

    public func onOrientationChanged(deviceOrientation: UIDeviceOrientation) {
        let orientation = OrientationHandlerUtils.getOrientationFrom(deviceOrientation: deviceOrientation)
        self.eventManager.sendDeviceOrientationDidChange(orientationValue: orientation.rawValue)
        adaptInterfaceTo(deviceOrientation: orientation)
    }

    private func adaptInterfaceTo(deviceOrientation: Orientation) {
        if (isLocked) {
          return
        }

        if (lastInterfaceOrientation == deviceOrientation) {
          return
        }

        lastInterfaceOrientation = deviceOrientation
        self.eventManager.sendInterfaceOrientationDidChange(orientationValue: lastInterfaceOrientation.rawValue)
      }
}
