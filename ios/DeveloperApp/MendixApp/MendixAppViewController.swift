import UIKit
import MendixNative

class MendixAppViewController: UIViewController, ReactNativeDelegateInternal {
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
    SceneDelegate.delegateInstance()?.window?.overrideUserInterfaceStyle = .unspecified
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
    SceneDelegate.delegateInstance()?.window?.overrideUserInterfaceStyle = .light
  }

  func onAppClosed() {
    if let sceneDelegate = SceneDelegate.delegateInstance(), sceneDelegate.previewingSampleApp == true {
      sceneDelegate.previewingSampleApp = false
      StorageHelper.clearAll()
    }
    self.navigationController?.popViewController(animated: true)
  }
}
