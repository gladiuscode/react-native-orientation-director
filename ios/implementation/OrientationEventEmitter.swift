//
//  OrientationEventEmitter.swift
//  react-native-orientation-handler
//
//  Created by Mirko Quaglia on 18/05/2024.
//

import Foundation

@objc public class OrientationEventEmitter : NSObject {
    @objc public weak var delegate: OrientationEventEmitterDelegate? = nil
}

@objc public protocol OrientationEventEmitterDelegate {
    func sendEvent(name: String, result: NSDictionary)
}

public extension OrientationEventEmitter {
  
  enum Event: String, CaseIterable {
    case OrientationDidChange
  }

  @objc
  static var supportedEvents: [String] {
    return Event.allCases.map(\.rawValue);
  }
}
