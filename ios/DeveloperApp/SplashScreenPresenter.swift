import Foundation
import MendixNative

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if let rootView {
      RNBootSplash.showStoryBoard(.launchScreen, rootView: rootView)
    }
  }

  func hide() {
    RNBootSplash.hideStoryBoard()
  }
}
