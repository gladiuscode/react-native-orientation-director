//
//  OrientationEventEmitter.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

@objc public class EventManager : NSObject {
    @objc public weak var delegate: OrientationEventEmitterDelegate? = nil

    func sendDeviceOrientationDidChange(orientationValue: Int) {
        guard let delegate = delegate else {
            return
        }

        if (!delegate.isJsListening) {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("orientation", orientationValue))
        delegate.sendEvent(name: Event.DeviceOrientationDidChange.rawValue, params: params as NSDictionary)
    }

    func sendInterfaceOrientationDidChange(orientationValue: Int) {
        guard let delegate = delegate else {
            return
        }

        if (!delegate.isJsListening) {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("orientation", orientationValue))
        delegate.sendEvent(name: Event.InterfaceOrientationDidChange.rawValue, params: params as NSDictionary)
    }
    
    func sendLockDidChange(value: Bool) {
        guard let delegate = delegate else {
            return
        }

        if (!delegate.isJsListening) {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("locked", value))
        delegate.sendEvent(name: Event.LockDidChange.rawValue, params: params as NSDictionary)
    }
}

@objc public protocol OrientationEventEmitterDelegate {
    var isJsListening: Bool { get set }

    func sendEvent(name: String, params: NSDictionary)
}

public extension EventManager {

  enum Event: String, CaseIterable {
    case DeviceOrientationDidChange
    case InterfaceOrientationDidChange
    case LockDidChange
  }

  @objc static var supportedEvents: [String] {
    return Event.allCases.map(\.rawValue);
  }
}
