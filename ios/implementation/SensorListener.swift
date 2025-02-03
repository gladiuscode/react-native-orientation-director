//
//  OrientationSensorListener.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

public class SensorListener {
    private var onOrientationDidChangeCallback: ((_ deviceOrientation: UIDeviceOrientation) -> Void)?

    init() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(orientationDidChange),
            name: UIDevice.orientationDidChangeNotification,
            object: nil
        )
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

    func setOnOrientationDidChange(callback: @escaping (_ deviceOrientation: UIDeviceOrientation) -> Void) {
        self.onOrientationDidChangeCallback = callback
    }

    @objc func orientationDidChange(_ notification: Notification) {
        guard let onOrientationDidChangeCallback = self.onOrientationDidChangeCallback else {
            return
        }

        onOrientationDidChangeCallback(UIDevice.current.orientation)
    }
}
