import UIKit

func getWarningFilterValue() -> WarningsFilter {
#if DEBUG
  return .all
#else
  return AppPreferences.devModeEnabled() ? .partial : .none
#endif
}

func applyMendixLogoNavigationBar(_ viewController: UIViewController) {
  if let navBarFrame = viewController.navigationController?.navigationBar.frame {
    let imageView = UIImageView(image: UIImage(named: "mendix logo"))
    imageView.frame = CGRect(x: 0, y: 0, width: navBarFrame.width, height: navBarFrame.height - 12.0)
    imageView.contentMode = .scaleAspectFit

    let titleView = UIView(frame: imageView.frame)
    titleView.addSubview(imageView)

    viewController.navigationItem.titleView = titleView
  }
}
