//
//  BundleManager.swift
//  react-native-orientation-director
//
//  Created by gladiuscode on 26/05/2024.
//

import Foundation

class BundleManager {

    private var supportedInterfaceOrientations: [UIInterfaceOrientationMask] = [UIInterfaceOrientationMask.all]

    init() {
        supportedInterfaceOrientations = readSupportedInterfaceOrientations()
    }

    func getSupportedInterfaceOrientations() -> [UIInterfaceOrientationMask] {
        return supportedInterfaceOrientations
    }

    private func readSupportedInterfaceOrientations() -> [UIInterfaceOrientationMask] {
        let orientations = Bundle.main.object(forInfoDictionaryKey: "UISupportedInterfaceOrientations") as? [String]

        guard let orientations = orientations else {
            return [UIInterfaceOrientationMask.all]
        }

        return orientations.compactMap { orientation in
            switch orientation {
            case "UIInterfaceOrientationPortrait":
                return UIInterfaceOrientationMask.portrait
            case "UIInterfaceOrientationLandscapeLeft":
                return UIInterfaceOrientationMask.landscapeLeft
            case "UIInterfaceOrientationLandscapeRight":
                return UIInterfaceOrientationMask.landscapeRight
            case "UIInterfaceOrientationPortraitUpsideDown":
                return UIInterfaceOrientationMask.portraitUpsideDown
            default:
                return UIInterfaceOrientationMask.allButUpsideDown
            }
        }
    }

}
