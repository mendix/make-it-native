import UIKit

class SwipeBackNavigationController: UINavigationController, UIGestureRecognizerDelegate {
  override func viewDidLoad() {
    super.viewDidLoad()
    interactivePopGestureRecognizer?.isEnabled = true
    interactivePopGestureRecognizer?.delegate = self
  }

  func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    return true
  }
}
