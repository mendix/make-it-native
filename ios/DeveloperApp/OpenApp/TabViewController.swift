//
//  TabViewController.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 26/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import UIKit

class TabViewController: UITabBarController {
  var tabBarHeight: CGFloat = 0
  override func viewDidLoad() {
    super.viewDidLoad()
    tabBarHeight = self.tabBar.frame.height
  }
  
  func setSelectedIndex(index:Int){
    guard let appDelegate = UIApplication.shared.delegate as? AppDelegate,
          let tabBarController = appDelegate.window.rootViewController as? UITabBarController else {
      return
    }
    tabBarController.selectedIndex = index
  }
}
