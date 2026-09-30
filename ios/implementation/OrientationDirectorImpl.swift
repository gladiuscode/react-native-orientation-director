//
//  OrientationDirectorImpl.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation
import UIKit

@objcMembers public class OrientationDirectorImpl: NSObject {
    private static let TAG = "OrientationDirectorImpl"

    private let bundleManager: BundleManager = BundleManager()
    private let utils: Utils = Utils()
    private let sensorListener: SensorListener = SensorListener()
    private lazy var sceneGeometryListener: SceneGeometryListener = SceneGeometryListener(utils: utils)
    private let eventManager: EventManager = EventManager()
    private var initialSupportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all
    private var lastInterfaceOrientation = Orientation.UNKNOWN
    private var lastDeviceOrientation = Orientation.UNKNOWN
    private var isLocked = false

    /// # Only on iOS < 16
    /// This variable is needed to prevent a loop where
    /// we lock the interface to a specific orientation
    /// and the sensor picks it up, therefore triggering
    /// the orientation did change event.
    private var isLocking = false

    @objc public var supportedInterfaceOrientations: UIInterfaceOrientationMask = UIInterfaceOrientationMask.all

    @objc public override init() {
        super.init()

        sensorListener.setOnOrientationDidChange(callback: self.onOrientationChanged)

        initialSupportedInterfaceOrientations = initInitialSupportedInterfaceOrientations()
        lastInterfaceOrientation = initInterfaceOrientation()
        lastDeviceOrientation = initDeviceOrientation()
        isLocked = initIsLocked()

        supportedInterfaceOrientations = initialSupportedInterfaceOrientations

        sceneGeometryListener.setOnGeometryDidChange(callback: self.onSceneGeometryChanged)
        sceneGeometryListener.attach()
        // The scene might not be connected yet when this module is initialized
        DispatchQueue.main.async { [weak self] in
            self?.sceneGeometryListener.attach()
        }
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
        self.isLocking = true;
        let jsOrientation = utils.convertToOrientationFrom(jsValue: jsValue)
        let mask = utils.convertToMaskFrom(jsOrientation: jsOrientation)
        self.requestInterfaceUpdateTo(mask: mask)

        updateIsLockedTo(value: true)

        /// On iOS >= 16 the scene geometry listener reports the orientation
        /// actually applied by the system, which might differ from the requested
        /// one or ignore it entirely (e.g. iPhone Duo inner display).
        if #available(iOS 16.0, *) {
            self.isLocking = false
            return
        }

        let orientationCanBeUpdatedDirectly = jsOrientation != Orientation.LANDSCAPE
        if orientationCanBeUpdatedDirectly {
            updateLastInterfaceOrientationTo(value: jsOrientation)
            self.isLocking = false;
            return
        }

        let lastInterfaceOrientationIsAlreadyInLandscape = lastInterfaceOrientation == Orientation.LANDSCAPE_RIGHT || lastInterfaceOrientation == Orientation.LANDSCAPE_LEFT
        if lastInterfaceOrientationIsAlreadyInLandscape {
            updateLastInterfaceOrientationTo(value: lastInterfaceOrientation)
            self.isLocking = false;
            return
        }

        let systemDefaultLandscapeOrientation = Orientation.LANDSCAPE_RIGHT
        updateLastInterfaceOrientationTo(value: systemDefaultLandscapeOrientation)
        self.isLocking = false;
    }

    @objc public func unlock() {
        self.requestInterfaceUpdateTo(mask: UIInterfaceOrientationMask.all)

        updateIsLockedTo(value: false)

        if #available(iOS 16.0, *) {
            return
        }

        self.adaptInterfaceTo(deviceOrientation: lastDeviceOrientation)
    }

    @objc public func resetSupportedInterfaceOrientations() {
        self.supportedInterfaceOrientations = self.initialSupportedInterfaceOrientations
        self.requestInterfaceUpdateTo(mask: self.supportedInterfaceOrientations)
        self.updateIsLockedTo(value: self.initIsLocked())

        /// On iOS >= 16 the scene geometry listener reports the orientation
        /// actually applied by the system.
        if #available(iOS 16.0, *) {
            return
        }

        let lastMask = utils.convertToMaskFrom(jsOrientation: lastInterfaceOrientation)
        let isLastMaskSupported = self.supportedInterfaceOrientations.contains(lastMask)
        if isLastMaskSupported {
            return
        }

        let supportedInterfaceOrientations = bundleManager.getSupportedInterfaceOrientations()
        if supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.portrait) {
            self.updateLastInterfaceOrientationTo(value: Orientation.PORTRAIT)
            return
        }
        if supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.landscapeRight) {
            self.updateLastInterfaceOrientationTo(value: Orientation.LANDSCAPE_RIGHT)
            return
        }
        if supportedInterfaceOrientations.contains(UIInterfaceOrientationMask.landscapeLeft) {
            self.updateLastInterfaceOrientationTo(value: Orientation.LANDSCAPE_LEFT)
            return
        }

        self.updateLastInterfaceOrientationTo(value: Orientation.PORTRAIT_UPSIDE_DOWN)
    }

    private func initInitialSupportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        let supportedInterfaceOrientations = bundleManager.getSupportedInterfaceOrientations()
        return supportedInterfaceOrientations.reduce(UIInterfaceOrientationMask()) { $0.union($1) }
    }

    private func initInterfaceOrientation() -> Orientation {
        return self.getOrientationFromInterface()
    }

    private func initDeviceOrientation() -> Orientation {
        return utils.convertToOrientationFrom(deviceOrientation: UIDevice.current.orientation)
    }

    private func initIsLocked() -> Bool {
        let supportedOrientations = bundleManager.getSupportedInterfaceOrientations()
        if supportedOrientations.count > 1 {
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
            let interfaceOrientation = self.utils.convertToInterfaceOrientationFrom(mask: mask)
            UIDevice.current.setValue(interfaceOrientation.rawValue, forKey: "orientation")
            UIViewController.attemptRotationToDeviceOrientation()
        }
    }

    private func onOrientationChanged(uiDeviceOrientation: UIDeviceOrientation) {
        let deviceOrientation = utils.convertToOrientationFrom(deviceOrientation: uiDeviceOrientation)
        updateLastDeviceOrientationTo(value: deviceOrientation)

        /// On iOS >= 16 the interface orientation is driven by the scene
        /// geometry listener, see onSceneGeometryChanged.
        if #available(iOS 16.0, *) {
            return
        }

        adaptInterfaceTo(deviceOrientation: deviceOrientation)
    }

    /// # Only on iOS >= 16
    /// The scene geometry is the source of truth for the interface orientation:
    /// it reflects what the system actually displays, even when the supported
    /// interface orientations are ignored (e.g. iPhone Duo inner display) and
    /// when the scene moves to another screen (fold / unfold).
    private func onSceneGeometryChanged(scene: UIWindowScene) {
        let interfaceOrientation = utils.getInterfaceOrientation(scene: scene)
        if interfaceOrientation == .unknown {
            return
        }

        let newInterfaceOrientation = utils.convertToOrientationFrom(uiInterfaceOrientation: interfaceOrientation)
        updateLastInterfaceOrientationTo(value: newInterfaceOrientation)
    }

    /// # Only on iOS < 16
    private func adaptInterfaceTo(deviceOrientation: Orientation) {
        let supportsLandscape = self.supportedInterfaceOrientations.contains(.landscape)
        if isLocked && !supportsLandscape {
            return
        }

        if deviceOrientation == Orientation.FACE_UP || deviceOrientation == Orientation.FACE_DOWN {
            return
        }

        let newInterfaceOrientation = self.getOrientationFromInterface()
        updateLastInterfaceOrientationTo(value: newInterfaceOrientation)
    }

    private func updateIsLockedTo(value: Bool) {
        eventManager.sendLockDidChange(value: value)
        isLocked = value
    }

    private func updateLastDeviceOrientationTo(value: Orientation) {
        if value == lastDeviceOrientation {
            return
        }

        if !self.isLocking {
            self.eventManager.sendDeviceOrientationDidChange(value: value.rawValue)
        }

        lastDeviceOrientation = value
    }

    private func updateLastInterfaceOrientationTo(value: Orientation) {
        if value == lastInterfaceOrientation {
            return
        }

        self.eventManager.sendInterfaceOrientationDidChange(value: value.rawValue)
        lastInterfaceOrientation = value
    }

    private func getOrientationFromInterface() -> Orientation {
        let interfaceOrientation = utils.getInterfaceOrientation()
        return utils.convertToOrientationFrom(uiInterfaceOrientation: interfaceOrientation)
    }
}
