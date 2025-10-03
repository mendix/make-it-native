import Foundation
import UIKit
import SwiftUI

class LaunchTutorialViewController: UIViewController {
  override func viewDidLoad() {
    super.viewDidLoad()
    
    if(showOnboarding()){
      setupSwiftUIScreen()
    }
  }
  
  override func viewDidAppear(_ animated: Bool) {
    if(!showOnboarding()){
      closeOnboarding()
    }
    // If onboarding is shown, onboardingShown will set true.
    else {
      UserDefaults.standard.set(true, forKey: SettingsBundleHelper.tutorialShownOnboarding)
    }
  }
  
  private func closeOnboarding() {
    AppDelegate.instance()?.changeRootViewToOpenApp()
  }
  
  private func setupSwiftUIScreen(){
    // Set up Swift UI screen.
    let tutorialUi = UIHostingController(rootView: OnboardingView(closeOnboarding: closeOnboarding))
    
    view.addSubview(tutorialUi.view)
    
    // Adjust frame constraints.
    tutorialUi.view.translatesAutoresizingMaskIntoConstraints = false
    let constraints = [
      tutorialUi.view.topAnchor.constraint(equalTo: view.topAnchor),
      tutorialUi.view.leftAnchor.constraint(equalTo: view.leftAnchor),
      tutorialUi.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
      tutorialUi.view.rightAnchor.constraint(equalTo: view.rightAnchor)
    ]
    
    NSLayoutConstraint.activate(constraints)
  }
  
}
