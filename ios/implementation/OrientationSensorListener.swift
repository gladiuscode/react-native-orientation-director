//
//  OrientationSensorListener.swift
//  react-native-orientation-handler
//
//  Created by Mirko Quaglia on 18/05/2024.
//

import Foundation

public class OrientationSensorListener {
    private var onOrientationChangedCallback: ((_ deviceOrientation: UIDeviceOrientation) -> Void)? = nil
    
    public init() {
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
    
    func setOnOrientationChangedCallback(callback: @escaping (_ deviceOrientation: UIDeviceOrientation) -> Void) {
        self.onOrientationChangedCallback = callback
    }

    @objc func orientationDidChange(_ notification: Notification) {
        guard let onOrientationChangedCallback = self.onOrientationChangedCallback else {
            return
        }
        
        onOrientationChangedCallback(UIDevice.current.orientation)
    }
}
