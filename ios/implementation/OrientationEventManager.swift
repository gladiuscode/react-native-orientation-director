//
//  OrientationEventEmitter.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation

@objc public class OrientationEventManager : NSObject {
    @objc public weak var delegate: OrientationEventEmitterDelegate? = nil

    func sendDeviceOrientationDidChange(orientationValue: Int) {
        guard let delegate = delegate else {
            return
        }

        if (!delegate.isJsListening) {
            return
        }

        let params = Dictionary(dictionaryLiteral: ("orientation", orientationValue))
        print(Event.InterfaceOrientationDidChange)
        print(params)
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
        print(Event.InterfaceOrientationDidChange)
        print(params)
        delegate.sendEvent(name: Event.InterfaceOrientationDidChange.rawValue, params: params as NSDictionary)
    }
}

@objc public protocol OrientationEventEmitterDelegate {
    var isJsListening: Bool { get set }

    func sendEvent(name: String, params: NSDictionary)
}

public extension OrientationEventManager {

  enum Event: String, CaseIterable {
    case DeviceOrientationDidChange
    case InterfaceOrientationDidChange
  }

  @objc static var supportedEvents: [String] {
    return Event.allCases.map(\.rawValue);
  }
}
