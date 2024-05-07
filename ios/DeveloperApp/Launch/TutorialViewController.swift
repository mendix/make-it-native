import Foundation

class LaunchTutorialViewController: UIViewController {
  @IBOutlet weak var skipButton: UIButton!
  @IBOutlet weak var nextButton: UIButton!
  
  var tutorialPageViewController: TutorialPageViewController?
  
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    switch segue.destination {
      case let viewController as TutorialPageViewController:
        self.tutorialPageViewController = viewController
      default:
        break
    }
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
  }
  
  @IBAction func skipButtonPressed(_ sender: UIButton) {
  }
  
  @IBAction func nextButtonPressed(_ sender: UIButton) {
    guard let tutorialDataSource = self.tutorialPageViewController?.dataSource as? TutorialDataSource else { return }
    guard let currentViewController = self.tutorialPageViewController?.viewControllers?.first else { return }
    guard let nextViewController = tutorialDataSource.pageViewController(self.tutorialPageViewController!, viewControllerAfter: currentViewController ) else { return }
    
    self.tutorialPageViewController?.setViewControllers([nextViewController], direction: .forward, animated: true, completion: nil)
  }
}
