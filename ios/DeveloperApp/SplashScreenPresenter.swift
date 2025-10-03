import Foundation
import MendixNative

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if let rootView {
      RNSplashScreen.showStoryBoard(.launchScreen, rootView: rootView)
    }
  }

  func hide() {
    RNSplashScreen.hideStoryBoard()
  }
}
