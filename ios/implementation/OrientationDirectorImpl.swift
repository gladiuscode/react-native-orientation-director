//
//  OrientationDirectorImpl.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation
import UIKit

@objc public class OrientationDirectorImpl : NSObject {
    private static let TAG = "OrientationDirectorImpl"
    private let sensorListener: OrientationSensorListener
    private let eventManager: OrientationEventManager
    private var initialSupportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all
    private var lastInterfaceOrientation = Orientation.UNKNOWN
    private var lastDeviceOrientation = Orientation.UNKNOWN

    @objc public var supportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all
    @objc public var isLocked = false

    @objc public override init() {
        eventManager = OrientationEventManager()
        sensorListener = OrientationSensorListener()
        super.init()
        sensorListener.setOnOrientationChanged(callback: self.onOrientationChanged)
        initialSupportedInterfaceOrientations = initInitialSupportedInterfaceOrientations()
        lastInterfaceOrientation = initInterfaceOrientation()
        lastDeviceOrientation = initDeviceOrientation()
        isLocked = initIsLocked()

        supportedInterfaceOrientations = initialSupportedInterfaceOrientations
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///         EVENT EMITTER SETUP
    @objc public func setEventManager(delegate: OrientationEventEmitterDelegate) {
        self.eventManager.delegate = delegate
    }
    ///
    //////////////////////////////////////////////////////////////////////////////////////////

    @objc public func getInterfaceOrientation() -> Orientation {
        return lastInterfaceOrientation
    }

    @objc public func getDeviceOrientation() -> Orientation {
        return lastDeviceOrientation
    }

    @objc public func lockTo(jsOrientation: NSNumber) {
        let orientation = OrientationDirectorUtils.getOrientationFrom(jsOrientation: jsOrientation)
        let mask = OrientationDirectorUtils.getMaskFrom(orientation: orientation)

        self.requestInterfaceUpdateTo(mask: mask)

        eventManager.sendInterfaceOrientationDidChange(orientationValue: orientation.rawValue)
        lastInterfaceOrientation = orientation
        updateIsLockedTo(value: true)
    }

    @objc public func unlock() {
        self.requestInterfaceUpdateTo(mask: UIInterfaceOrientationMask.all)

        let deviceOrientation = OrientationDirectorUtils.getOrientationFrom(deviceOrientation: UIDevice.current.orientation)
        updateIsLockedTo(value: false)
        self.adaptInterfaceTo(deviceOrientation: deviceOrientation)
    }
    
    @objc public func resetSupportedInterfaceOrientations() {
        self.supportedInterfaceOrientations = self.initialSupportedInterfaceOrientations
        self.requestInterfaceUpdateTo(mask: self.supportedInterfaceOrientations)
        self.updateIsLockedTo(value: self.initIsLocked())

        let lastMask = OrientationDirectorUtils.getMaskFrom(orientation: lastInterfaceOrientation)
        let isLastInterfaceOrientationAlreadySupported = self.supportedInterfaceOrientations.contains(lastMask)
        if (isLastInterfaceOrientationAlreadySupported) {
            return
        }

        let supportedInterfaceOrientations = OrientationDirectorUtils.readSupportedInterfaceOrientationsFromBundle()
        guard let firstSupportedInterfaceOrientation = supportedInterfaceOrientations.first else {
            return
        }
        
        let orientation = OrientationDirectorUtils.getOrientationFrom(mask: firstSupportedInterfaceOrientation)
        self.updateLastInterfaceOrientation(value: orientation)
    }

    private func initInitialSupportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        let supportedInterfaceOrientations = OrientationDirectorUtils.readSupportedInterfaceOrientationsFromBundle()
        return supportedInterfaceOrientations.reduce(UIInterfaceOrientationMask()) { $0.union($1) }
    }

    private func initInterfaceOrientation() -> Orientation {
        let interfaceOrientation = OrientationDirectorUtils.getInterfaceOrientation()
        return OrientationDirectorUtils.getOrientationFrom(uiInterfaceOrientation: interfaceOrientation)
    }

    private func initDeviceOrientation() -> Orientation {
        return OrientationDirectorUtils.getOrientationFrom(deviceOrientation: UIDevice.current.orientation)
    }

    private func initIsLocked() -> Bool {
        let supportedOrientations = OrientationDirectorUtils.readSupportedInterfaceOrientationsFromBundle()
        if (supportedOrientations.count > 1) {
            return false
        }

        return supportedOrientations.first != UIInterfaceOrientationMask.all
    }

    private func requestInterfaceUpdateTo(mask: UIInterfaceOrientationMask) {
        self.supportedInterfaceOrientations = mask

        DispatchQueue.main.async {
            if #available(iOS 16.0, *) {
                guard let window = OrientationDirectorUtils.getCurrentWindow() else {
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
                    print("\(OrientationDirectorImpl.TAG) - requestGeometryUpdate error", error)
                }
            } else {
                UIDevice.current.setValue(mask.rawValue, forKey: "orientation")
                UIViewController.attemptRotationToDeviceOrientation()
            }
        }
    }

    private func onOrientationChanged(uiDeviceOrientation: UIDeviceOrientation) {
        let deviceOrientation = OrientationDirectorUtils.getOrientationFrom(deviceOrientation: uiDeviceOrientation)
        self.eventManager.sendDeviceOrientationDidChange(orientationValue: deviceOrientation.rawValue)
        lastDeviceOrientation = deviceOrientation
        adaptInterfaceTo(deviceOrientation: deviceOrientation)
    }

    private func adaptInterfaceTo(deviceOrientation: Orientation) {
        if (isLocked) {
          return
        }

        if (lastInterfaceOrientation == deviceOrientation) {
          return
        }

        if (deviceOrientation == Orientation.FACE_UP || deviceOrientation == Orientation.FACE_DOWN) {
          return
        }

        updateLastInterfaceOrientation(value: deviceOrientation)
    }

    private func updateIsLockedTo(value: Bool) {
        eventManager.sendLockDidChange(value: value)
        isLocked = value
    }
    
    private func updateLastInterfaceOrientation(value: Orientation) {
        self.eventManager.sendInterfaceOrientationDidChange(orientationValue: value.rawValue)
        lastInterfaceOrientation = value
    }
}
