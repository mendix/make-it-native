import Foundation
import UIKit

class AboutViewController: UIViewController {
  @IBOutlet weak var referenceGuideButton: UIButton!
  @IBOutlet weak var howtoButton: UIButton!

  override func viewDidLoad() {
    super.viewDidLoad()

    referenceGuideButton.layer.cornerRadius = 8.0
    howtoButton.layer.cornerRadius = 8.0
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)

    applyMendixLogoNavigationBar(self)
  }

  override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
    super.viewWillTransition(to: size, with: coordinator)

    applyMendixLogoNavigationBar(self)
  }

  @IBAction func navigateToReferenceGuide(_ sender: Any) {
    if let url = getUrlFromBundle("ReferenceGuideUrl") {
      navigateTo(url)
    }
  }

  @IBAction func navigateToHowtos(_ sender: Any) {
    if let url = getUrlFromBundle("TutorialResourceUrl") {
      navigateTo(url)
    }
  }

  private func getUrlFromBundle(_ reference: String) -> String? {
    return Bundle.main.object(forInfoDictionaryKey: reference) as? String
  }

  private func navigateTo(_ urlString: String) {
    if let url = URL(string: urlString) {
      if #available(iOS 10.0, *) {
        UIApplication.shared.open(url, options: [:], completionHandler: nil)
      } else {
        UIApplication.shared.openURL(url)
      }
    }
  }
}
