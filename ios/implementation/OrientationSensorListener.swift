//
//  OrientationSensorListener.swift
//  react-native-orientation-handler
//
//  Created by Mirko Quaglia on 18/05/2024.
//

import Foundation

@objc public class OrientationSensorListener: OrientationEventEmitter {
    
    override public init() {
        super.init()
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
        let deviceOrientation = UIDevice.current.orientation
        
        guard let delegate = self.delegate else {
            return
        }
        
        delegate.sendEvent(name: OrientationEventEmitter.Event.OrientationDidChange.rawValue, result: ["orientation":deviceOrientation.rawValue])
    }
}
