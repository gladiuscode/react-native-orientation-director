//
//  OrientationSensorListener.swift
//  react-native-orientation-handler
//
//  Created by Mirko Quaglia on 18/05/2024.
//

import Foundation

public class OrientationSensorListener {
    private let eventEmitter: OrientationEventManager
    
    public init(fromEventEmitter: OrientationEventManager) {
        self.eventEmitter = fromEventEmitter
        
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

    @objc func orientationDidChange(_ notification: Notification) {
        self.eventEmitter.sendDeviceOrientationDidChange(orientationValue: UIDevice.current.orientation.rawValue)
    }
}
