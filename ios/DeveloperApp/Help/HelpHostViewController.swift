//
//  HelpHostViewController.swift
//  DeveloperApp
//
//  Created by Selim Ustel on 15/06/2023.
//  Copyright © 2023 Mendix. All rights reserved.
//

import UIKit
import SwiftUI

class HelpHostViewController: UIHostingController<HelpView> {
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder, rootView: HelpView())
  }
}
