//
//  OrientationEventEmitter.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

public class EventManager: NSObject {
    public weak var delegate: OrientationEventEmitterDelegate?

    func sendDeviceOrientationDidChange(value: Int) {
        guard let delegate = delegate else {
            return
        }

        if !delegate.isJsListening {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("orientation", value))
        delegate.emitOnDeviceOrientationDidChange(params: params as NSDictionary)
    }

    func sendInterfaceOrientationDidChange(value: Int) {
        guard let delegate = delegate else {
            return
        }

        if !delegate.isJsListening {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("orientation", value))
        delegate.emitOnInterfaceOrientationDidChange(params: params as NSDictionary)
    }

    func sendLockDidChange(value: Bool) {
        guard let delegate = delegate else {
            return
        }

        if !delegate.isJsListening {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("locked", value))
        delegate.emitOnLockChanged(params: params as NSDictionary)
    }
}

@objc public protocol OrientationEventEmitterDelegate {
    var isJsListening: Bool { get set }

    func emitOnLockChanged(params: NSDictionary)
    func emitOnDeviceOrientationDidChange(params: NSDictionary)
    func emitOnInterfaceOrientationDidChange(params: NSDictionary)
}
