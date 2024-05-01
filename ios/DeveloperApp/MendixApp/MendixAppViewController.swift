import UIKit

class MendixAppViewController: UIViewController, ReactNativeDelegate {
  override func becomeFirstResponder() -> Bool {
    return true;
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    
    // Set all orientations available while launching the mendix app.
    AppDelegate.orientationLock = .all
    
    let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
    appDelegate.window?.overrideUserInterfaceStyle = .unspecified


    ReactNative.instance.delegate = self
    ReactNative.instance.start()
  }
  
  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    becomeFirstResponder()
    
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
    let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
    appDelegate.window?.overrideUserInterfaceStyle = .light
  }

  func onAppClosed() {
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    if (appDelegate.previewingSampleApp == true) {
      appDelegate.previewingSampleApp = false
      ReactNative.instance.clearData()
    }
    self.navigationController?.popViewController(animated: true)
  }
}
