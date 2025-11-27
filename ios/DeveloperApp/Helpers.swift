import UIKit

func getWarningFilterValue() -> WarningsFilter {
#if DEBUG
  return .all
#else
  return AppPreferences.devModeEnabled ? .partial : .none
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

func getUrlFromBundle(_ reference: String) -> String? {
  return Bundle.main.object(forInfoDictionaryKey: reference) as? String
}

func navigateTo(_ urlString: String) {
  if let url = URL(string: urlString) {
    if #available(iOS 10.0, *) {
      UIApplication.shared.open(url, options: [:], completionHandler: nil)
    } else {
      UIApplication.shared.openURL(url)
    }
  }
}

func saveUrlToUserDefaults(val:String){
  let urlKey  = SettingsBundleHelper.homeUrlTextFieldKey
  let userDefault = UserDefaults.standard
  userDefault.set(val, forKey: urlKey)
}

func getUrlFromUserDefaults() -> String{
  let urlKey  = SettingsBundleHelper.homeUrlTextFieldKey
  let userDefault = UserDefaults.standard
  return userDefault.string(forKey: urlKey) ?? ""
}

func showOnboarding()->Bool{
  let onboardingShown = UserDefaults.standard.bool(forKey: SettingsBundleHelper.tutorialShownOnboarding)
  return !onboardingShown
}

func getVersionString()->String{
  let version = UIApplication.appVersion()
  return "Version: \(version)"
}
