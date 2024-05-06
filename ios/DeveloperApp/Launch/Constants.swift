//
//  Constants.swift
//  DeveloperApp
//
//  Created by Diego Antonelli on 08/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import Foundation

extension UIApplication {
  struct Constants {
    static let CFBundleShortVersionString = "CFBundleShortVersionString"
    static let images = ["BackgroundA","BackgroundB","BackgroundC",
                         "BackgroundD","BackgroundE","BackgroundF",
                         "BackgroundG","BackgroundH","BackgroundI",
                         "BackgroundJ","BackgroundK","BackgroundL"]
    
    static let randomBackground = images.randomElement()!
    
    static let launchTexts = ["Getting ready...", "Baking in the oven...", "Crafting...", "Starting..."]
  }
  
  class func appVersion() -> String {
    return Bundle.main.object(forInfoDictionaryKey: Constants.CFBundleShortVersionString) as? String ?? ""
  }
  
  class func appBuild() -> String {
    return Bundle.main.object(forInfoDictionaryKey: kCFBundleVersionKey as String) as? String ?? ""
  }
  
  class func versionBuild() -> String {
    let version = appVersion(), build = appBuild()
    
    return version == build ? "v\(version)" : "v\(version)(\(build))"
  }
  
  class func randomBackgroundImage() -> UIImage {
    return UIImage(imageLiteralResourceName: Constants.randomBackground)
  }
}
