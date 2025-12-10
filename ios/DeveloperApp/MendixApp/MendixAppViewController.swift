import UIKit
import MendixNative
import React_RCTAppDelegate

class MendixAppViewController: UIViewController, ReactNativeDelegate {
  override func becomeFirstResponder() -> Bool {
    return true
  }
  
  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .darkContent
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Set all orientations available while launching the mendix app.
    AppDelegate.orientationLock = .all
    AppDelegate.delegateInstance()?.window.overrideUserInterfaceStyle = .unspecified
    ReactNative.shared.delegate = self
    ReactNative.shared.start()
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    _ = becomeFirstResponder()
    setNeedsStatusBarAppearanceUpdate()
  }
  
  override func viewWillDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    // Set orientation to only portrait mode, while exiting the mendix app.
    AppDelegate.orientationLock = .portrait
    AppDelegate.delegateInstance()?.window.overrideUserInterfaceStyle = .light
  }

  func onAppClosed() {
    if let appDelegate = AppDelegate.delegateInstance(), appDelegate.previewingSampleApp == true {
      appDelegate.previewingSampleApp = false
      StorageHelper.clearAll()
    }
    self.navigationController?.popViewController(animated: true)
  }
}
