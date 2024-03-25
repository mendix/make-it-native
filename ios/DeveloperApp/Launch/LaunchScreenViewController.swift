//
//  LaunchScreenViewController.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 27/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import UIKit
import SwiftUI

class LaunchScreenViewController: UIViewController {
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    setupLaunchScreenView()
  }
  
  func setupLaunchScreenView(){
    let launchUI = UIHostingController(rootView: LaunchScreenUIView().environmentObject(LaunchScreenStateManager()))
    
    view.addSubview(launchUI.view)
    launchUI.view.translatesAutoresizingMaskIntoConstraints = false
    
    let constraints = [
      launchUI.view.topAnchor.constraint(equalTo: view.topAnchor),
      launchUI.view.leftAnchor.constraint(equalTo: view.leftAnchor),
      launchUI.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
      launchUI.view.rightAnchor.constraint(equalTo: view.rightAnchor)
    ]
    
    NSLayoutConstraint.activate(constraints)
  }
}
