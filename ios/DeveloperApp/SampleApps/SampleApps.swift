//
//  SampleApps.swift
//  DeveloperApp
//
//  Created by Uraz Akgültan on 17/08/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import Foundation

class SampleAppsRoot: Codable {
  let apps: [SampleApp]
  
  init(apps: [SampleApp]) {
    self.apps = apps
  }
}

class SampleApp: Codable, Identifiable{
  
  let id: String
  let name: String
  let description: String?
  let runtimeUrl: String
  let backgroundImageName: String?
  
  init(id: String, name: String, description: String?, runtimeUrl: String, backgroundImageName: String?) {
    self.id = id
    self.name = name
    self.description = description
    self.runtimeUrl = runtimeUrl
    self.backgroundImageName = backgroundImageName
  }
}
