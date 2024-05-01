import UIKit

class OpenAppViewController: UIViewController {
  override func viewDidLoad() {
    super.viewDidLoad()
    
    self.navigationController?.navigationBar.setBackgroundImage(UIImage(), for: .default)
    
    let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
    if (appDelegate.shouldLaunchLastApp) {
      appDelegate.shouldLaunchLastApp = false;
      self.performSegue(withIdentifier: "MendixApp", sender: nil)
    }
  }
}
