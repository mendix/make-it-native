//
//  LaunchScreenStateManager.swift
//  DeveloperApp
//
//  Created by Diego Antonelli on 09/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import Foundation

final class LaunchScreenStateManager: ObservableObject {

  @MainActor @Published private(set) var state: LaunchScreenStep = .initialPointOfAnimation

  @MainActor func dismiss() {
    Task {
      state = .finalPointOfAnimation
      
      if #available(iOS 16.0, *) {
        try? await Task.sleep(for: Duration.seconds(1))
      } else {
        try? await Task.sleep(nanoseconds: UInt64(1_000_000_000))
      }
      
    }
  }
}
