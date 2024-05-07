import Foundation
import UIKit

class LaunchTutorialViewController: UIViewController {
    @IBOutlet weak var skipButton: UIButton!
    @IBOutlet weak var nextButton: UIButton!

    private var tutorialPageViewController: TutorialPageViewController?

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

        self.tutorialPageViewController!.delegate = self
    }
  
    override func viewDidAppear(_ animated: Bool) {
        let showTutorial = UserDefaults.standard.object(forKey: SettingsBundleHelper.ShowTutorialOnLaunch) != nil ? UserDefaults.standard.bool(forKey: SettingsBundleHelper.ShowTutorialOnLaunch): true
        if (!showTutorial) {
          closeTutorial()
          return
        }
    }

    @IBAction func nextButtonPressed(_ sender: UIButton) {
        guard let nextViewController = self.tutorialPageViewController!.goToNext() else {
          closeTutorial()
          return
        }

        let isLast = self.tutorialPageViewController!.isLast(nextViewController)
        self.updateButtons(isLast)
    }

    @IBAction func skipButtonPressed(_ sender: UIButton) {
        closeTutorial()
    }

    private func closeTutorial() {
        UserDefaults.standard.set(false, forKey: SettingsBundleHelper.ShowTutorialOnLaunch)
        self.performSegue(withIdentifier: "OpenAppSegue", sender: nil)
    }

    private func updateButtons(_ isLastPage: Bool) {
        if isLastPage {
          self.skipButton.isHidden = true
          self.nextButton.setTitle("GOT IT!", for: UIControlState.normal)
        } else {
          self.skipButton.isHidden = false
          self.nextButton.setTitle("NEXT", for: UIControlState.normal)
        }
    }
}

extension LaunchTutorialViewController: UIPageViewControllerDelegate {
    func pageViewController(_ pageViewController: UIPageViewController, willTransitionTo pendingViewControllers: [UIViewController]) {
        self.updateButtons(self.tutorialPageViewController?.isLast(pendingViewControllers.last!) ?? false)
    }

    func pageViewController(_ pageViewController: UIPageViewController, didFinishAnimating finished: Bool, previousViewControllers: [UIViewController], transitionCompleted completed: Bool) {
        if !completed {
          self.updateButtons(self.tutorialPageViewController?.isLast(previousViewControllers.last!) ?? false)
        }
    }
}
