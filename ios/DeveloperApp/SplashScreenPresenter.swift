import Foundation

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if (rootView != nil) {
      RNBootSplash.showStoryBoard("LaunchScreen", rootView: rootView!)
    }
  }

  func hide() {
    RNBootSplash.hideStoryBoard()
  }
}
