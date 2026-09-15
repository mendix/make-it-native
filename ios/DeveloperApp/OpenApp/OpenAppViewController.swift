import UIKit

class OpenAppViewController: UIViewController {
  override func viewDidLoad() {
    super.viewDidLoad()
    
    self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
    
    if let sceneDelegate = SceneDelegate.delegateInstance(), sceneDelegate.shouldLaunchLastApp {
      sceneDelegate.shouldLaunchLastApp = false;
      self.performSegue(withIdentifier: "MendixApp", sender: nil)
    }
  }
}
