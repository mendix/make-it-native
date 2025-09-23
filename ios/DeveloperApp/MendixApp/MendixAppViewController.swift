import UIKit
import MendixNative
import React_RCTAppDelegate

class MendixAppViewController: UIViewController, ReactNativeDelegate {
  override func becomeFirstResponder() -> Bool {
    return true
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Set all orientations available while launching the mendix app.
    AppDelegate.orientationLock = .all
    
    if let delegate = AppDelegate.instance() {
      delegate.window.overrideUserInterfaceStyle = .unspecified
    }

    ReactNative.instance.delegate = self
    ReactNative.instance.start()
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    _ = becomeFirstResponder()
    
    if #available(iOS 13.0, *) {
      UIApplication.shared.statusBarStyle = .darkContent
    } else {
      UIApplication.shared.statusBarStyle = .default
    }
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    // Set orientation to only portrait mode, while exiting the mendix app.
    AppDelegate.orientationLock = .portrait
    
    // Force Light Mode
    if let delegate = AppDelegate.instance() {
      delegate.window.overrideUserInterfaceStyle = .light
    }
  }

  func onAppClosed() {
    if let appDelegate = AppDelegate.instance(), appDelegate.previewingSampleApp == true {
      appDelegate.previewingSampleApp = false
      ReactNative.instance.clearData()
    }
    self.navigationController?.popViewController(animated: true)
  }
}
