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

    private let bundleManager: BundleManager = BundleManager()
    private let utils: Utils = Utils()
    private let sensorListener: SensorListener = SensorListener()
    private let eventManager: EventManager = EventManager()
    private var initialSupportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all
    private var lastInterfaceOrientation = Orientation.UNKNOWN
    private var lastDeviceOrientation = Orientation.UNKNOWN
    private var isLocked = false

    @objc public var supportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all

    @objc public override init() {
        super.init()

        sensorListener.setOnOrientationDidChange(callback: self.onOrientationChanged)

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
    
    @objc public func getIsLocked() -> Bool {
        return isLocked
    }

    @objc public func lockTo(jsValue: NSNumber) {
        let jsOrientation = utils.convertToOrientationFrom(jsValue: jsValue)
        let mask = utils.convertToMaskFrom(jsOrientation: jsOrientation)
        self.requestInterfaceUpdateTo(mask: mask)

        updateIsLockedTo(value: true)
        updateLastInterfaceOrientationTo(value: jsOrientation)
    }

    @objc public func unlock() {
        self.requestInterfaceUpdateTo(mask: UIInterfaceOrientationMask.all)

        updateIsLockedTo(value: false)
        self.adaptInterfaceTo(deviceOrientation: lastDeviceOrientation)
    }

    @objc public func resetSupportedInterfaceOrientations() {
        self.supportedInterfaceOrientations = self.initialSupportedInterfaceOrientations
        self.requestInterfaceUpdateTo(mask: self.supportedInterfaceOrientations)
        self.updateIsLockedTo(value: self.initIsLocked())

        let lastMask = utils.convertToMaskFrom(jsOrientation: lastInterfaceOrientation)
        let isLastMaskSupported = self.supportedInterfaceOrientations.contains(lastMask)
        if (isLastMaskSupported) {
            return
        }

        let supportedInterfaceOrientations = bundleManager.getSupportedInterfaceOrientations()
        if (supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.portrait)) {
            self.updateLastInterfaceOrientationTo(value: Orientation.PORTRAIT)
            return
        }
        if (supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.landscapeRight)) {
            self.updateLastInterfaceOrientationTo(value: Orientation.LANDSCAPE_RIGHT)
            return
        }
        if (supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.landscapeLeft)) {
            self.updateLastInterfaceOrientationTo(value: Orientation.LANDSCAPE_LEFT)
            return
        }

        self.updateLastInterfaceOrientationTo(value: Orientation.PORTRAIT_UPSIDE_DOWN)
    }

    private func initInitialSupportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        let supportedInterfaceOrientations = bundleManager.getSupportedInterfaceOrientations()
        return supportedInterfaceOrientations.reduce(UIInterfaceOrientationMask()) { $0.union($1) }
    }

    // TODO: FIX BECAUSE IT ALWAYS RETURNS PORTRAIT AND ITS BROKEN
    private func initInterfaceOrientation() -> Orientation {
        let interfaceOrientation = utils.getInterfaceOrientation()
        return utils.convertToOrientationFrom(uiInterfaceOrientation: interfaceOrientation)
    }

    private func initDeviceOrientation() -> Orientation {
        return utils.convertToOrientationFrom(deviceOrientation: UIDevice.current.orientation)
    }

    private func initIsLocked() -> Bool {
        let supportedOrientations = bundleManager.getSupportedInterfaceOrientations()
        if (supportedOrientations.count > 1) {
            return false
        }

        return supportedOrientations.first != UIInterfaceOrientationMask.all
    }

    private func requestInterfaceUpdateTo(mask: UIInterfaceOrientationMask) {
        self.supportedInterfaceOrientations = mask

        if #available(iOS 16.0, *) {
            let window = utils.getCurrentWindow()

            guard let rootViewController = window?.rootViewController else {
                return
            }

            guard let windowScene = window?.windowScene else {
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

    private func onOrientationChanged(uiDeviceOrientation: UIDeviceOrientation) {
        let deviceOrientation = utils.convertToOrientationFrom(deviceOrientation: uiDeviceOrientation)
        self.eventManager.sendDeviceOrientationDidChange(orientationValue: deviceOrientation.rawValue)
        lastDeviceOrientation = deviceOrientation
        adaptInterfaceTo(deviceOrientation: deviceOrientation)
    }

    private func adaptInterfaceTo(deviceOrientation: Orientation) {
        if (isLocked) {
            return
        }

        if (deviceOrientation == Orientation.FACE_UP || deviceOrientation == Orientation.FACE_DOWN) {
            return
        }

        let newInterfaceOrientationMask = utils.convertToMaskFrom(deviceOrientation: deviceOrientation)
        let isSupported = self.supportedInterfaceOrientations.contains(newInterfaceOrientationMask)
        if (!isSupported) {
            return
        }

        let newInterfaceOrientation = utils.convertToOrientationFrom(mask: newInterfaceOrientationMask)
        if (newInterfaceOrientation == lastInterfaceOrientation) {
            return
        }

        updateLastInterfaceOrientationTo(value: newInterfaceOrientation)
    }

    private func updateIsLockedTo(value: Bool) {
        eventManager.sendLockDidChange(value: value)
        isLocked = value
    }

    private func updateLastInterfaceOrientationTo(value: Orientation) {
        self.eventManager.sendInterfaceOrientationDidChange(orientationValue: value.rawValue)
        lastInterfaceOrientation = value
    }
}
