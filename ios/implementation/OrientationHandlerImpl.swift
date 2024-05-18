//
//  OrientationHandlerImpl.swift
//  react-native-orientation-handler
//
//  Created by gladiuscode on 18/05/2024.
//

import Foundation
import UIKit

@objc public class OrientationHandlerImpl : NSObject {

    private static let TAG = "OrientationHandlerImpl"

    @objc public func getInterfaceOrientation() -> UIInterfaceOrientation {
        return OrientationHandlerUtils.getInterfaceOrientation()
    }

}
