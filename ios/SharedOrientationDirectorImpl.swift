//
//  SharedOrientationDirectorImpl.swift
//  react-native-orientation-director
//
//  Created by Mirko Quaglia on 14/03/26.
//

import Foundation

@objc public class SharedOrientationDirectorImpl: NSObject {
    @objc public static let shared = OrientationDirectorImpl()
}
