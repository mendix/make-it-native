import Foundation
import UIKit

class TutorialPageViewController: UIPageViewController {
  var tutorialDataSource: TutorialDataSource?
  
  override func viewDidLoad() {
    super.viewDidLoad()

    tutorialDataSource = self.dataSource as? TutorialDataSource
    
    if let firstVC = tutorialDataSource!.viewControllerAt(index: 0) {
      setViewControllers([firstVC], direction: .forward, animated: true, completion: nil)
    }

    let appearance = UIPageControl.appearance()
    appearance.pageIndicatorTintColor = UIColor.gray
    appearance.currentPageIndicatorTintColor = UIColor.white
    appearance.backgroundColor = UIColor.clear
  }
  
  // Make sure our pageviewcontroller takes the entire screen (including the bottom region)
  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    
    if let scrollView = view.subviews.first(where: { $0 is UIScrollView }),
      let pageControl = view.subviews.first(where: { $0 is UIPageControl }) {
      scrollView.frame = view.bounds
      view.bringSubview(toFront:pageControl)
    }
  }
  
  func goToNext() -> UIViewController? {
    guard let nextViewController = tutorialDataSource!.next(self) else { return nil }
    setViewControllers([nextViewController], direction: .forward, animated: true, completion: nil)
    return nextViewController
  }
  
  func isLast(_ viewController: UIViewController) -> Bool {
    return self.tutorialDataSource!.isLast(viewController) ?? true
  }
}
