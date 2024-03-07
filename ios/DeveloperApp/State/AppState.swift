//
//  AppState.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 06/07/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import Foundation

// Singleton & Observable Object for handling App's State Management.
class AppState: ObservableObject {
  static let shared = AppState()
  
  @Published var uiState: HomeUIState // Home UI State: idle, loading, error etc.
  @Published var url:String{ // Home Launch App URL value.
    didSet { // Save url to userdefaults, whenever it has changed.
      saveUrlToUserDefaults(val: url)
    }
  }
  @Published var clearCache: Bool // Home Clear Cache Toggle value.
  @Published var developerMode: Bool // Home Developer Mode Toggle value.
  
  init() {
    self.uiState = .idle
    self.url = getUrlFromUserDefaults()
    self.clearCache = true
    self.developerMode = false
  }
  
  func isLoading()-> Bool{
    return self.uiState == .loading
  }
  
  func isUrlEmpty()-> Bool{
    return self.url == ""
  }
  
  func setUrl(val:String){
    self.url = val
  }
  
  func setClearCache(val:Bool){
    self.clearCache = val
  }
  
  func setDeveloperMode(val:Bool){
    self.developerMode = val
  }
}

// Home UI State Types
enum HomeUIState: Equatable {
  case idle
  case loading
  case error(message: String)
  case deprecatedRuntime
  case updateAvailable
  case runtimeNotRunning(url: String)
  case packagerNotRunning(url: String)
}
