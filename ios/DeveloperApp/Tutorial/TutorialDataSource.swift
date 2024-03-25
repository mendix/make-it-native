import Foundation
import UIKit

class TutorialDataSource: NSObject {
  fileprivate lazy var pages: [UIViewController] = {
    return [
      self.getViewController(withIdentifier: "Tutorial1"),
      self.getViewController(withIdentifier: "Tutorial2"),
      self.getViewController(withIdentifier: "Tutorial3")
    ]
  }()
  
  fileprivate func getViewController(withIdentifier identifier: String) -> UIViewController {
    return UIStoryboard(name: "Tutorial", bundle: nil).instantiateViewController(withIdentifier: identifier)
  }
  
  func viewControllerAt(index: Int) -> UIViewController? {
    if index < 0 || index >= self.pages.count {
      return nil
    }
    return self.pages[index]
  }

  func isLast(_ controller: UIViewController) -> Bool? {
    return pages.last?.isEqual(controller)
  }
  
  func next(_ pageViewController: UIPageViewController) -> UIViewController? {
    let currentIndex = self.presentationIndex(for: pageViewController)
    guard let nextViewController = self.viewControllerAt(index: currentIndex+1) else { return nil }
    return nextViewController
  }
}

extension TutorialDataSource: UIPageViewControllerDataSource {
  func pageViewController(_ pageViewController: UIPageViewController, viewControllerBefore viewController: UIViewController) -> UIViewController? {
    guard let viewControllerIndex = pages.index(of: viewController) else { return nil }
    
    let previousIndex = viewControllerIndex - 1
    guard previousIndex >= 0 else { return nil }

    return pages[previousIndex]
  }
  
  func pageViewController(_ pageViewController: UIPageViewController, viewControllerAfter viewController: UIViewController) -> UIViewController? {
    guard let viewControllerIndex = pages.index(of: viewController) else { return nil }
    
    let nextIndex = viewControllerIndex + 1
    guard nextIndex < pages.count else { return nil }

    return pages[nextIndex]
  }
  
  func presentationCount(for pageViewController: UIPageViewController) -> Int {
    return self.pages.count
  }
  
  func presentationIndex(for pageViewController: UIPageViewController) -> Int {
    guard let currentViewController = pageViewController.viewControllers?.first else { return 0 }
    guard let index = self.pages.index(of: currentViewController) else { return 0 }
    return index
  }
}
