//
//  SceneGeometryListener.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 25/09/2026.
//

import Foundation
import UIKit

/// # Only on iOS >= 16
/// Observes the effective geometry of the current window scene.
/// This is the only reliable source of interface orientation changes:
/// - on foldable devices (iPhone Duo) folding / unfolding moves the scene
///   to another screen without triggering any device orientation change;
/// - the geometry is updated after UIDevice.orientationDidChangeNotification,
///   so reading it from the sensor callback would return a stale value.
/// https://developer.apple.com/documentation/uikit/uiwindowscene/effectivegeometry
public class SceneGeometryListener {
    private let utils: Utils
    private var onGeometryDidChangeCallback: ((UIWindowScene) -> Void)?
    private var observation: NSKeyValueObservation?
    private weak var observedScene: UIWindowScene?

    init(utils: Utils) {
        self.utils = utils

        NotificationCenter.default.addObserver(
            self,
            selector: #selector(sceneDidActivate),
            name: UIScene.didActivateNotification,
            object: nil
        )
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
        observation?.invalidate()
    }

    func setOnGeometryDidChange(callback: @escaping (UIWindowScene) -> Void) {
        self.onGeometryDidChangeCallback = callback
    }

    /// Starts observing the current window scene, if not already observed.
    /// The callback is invoked right after attaching so that the caller
    /// can sync its state with the current geometry.
    func attach() {
        attach(to: utils.getCurrentWindow()?.windowScene)
    }

    @objc private func sceneDidActivate(_ notification: Notification) {
        /// Apps can have multiple scenes: once the React Native scene is observed,
        /// activations of unrelated scenes must not replace it. A new scene is picked
        /// only when the observed one is gone, as observedScene is weak.
        if observedScene != nil {
            return
        }

        attach(to: notification.object as? UIWindowScene)
    }

    private func attach(to scene: UIWindowScene?) {
        guard #available(iOS 16.0, *) else {
            return
        }

        guard let scene = scene, scene.session.role == .windowApplication else {
            return
        }

        if observedScene === scene {
            return
        }

        observedScene = scene
        observation?.invalidate()
        observation = scene.observe(\.effectiveGeometry, options: [.new]) { [weak self] scene, _ in
            self?.notifyGeometryDidChange(scene: scene)
        }

        notifyGeometryDidChange(scene: scene)
    }

    private func notifyGeometryDidChange(scene: UIWindowScene) {
        guard Thread.isMainThread else {
            DispatchQueue.main.async { [weak self] in
                self?.notifyGeometryDidChange(scene: scene)
            }
            return
        }

        onGeometryDidChangeCallback?(scene)
    }
}
