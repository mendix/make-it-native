import Foundation

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if (rootView != nil) {
      RNSplashScreen.showStoryBoard("LaunchScreen", rootView: rootView!)
    }
  }

  func hide() {
    RNSplashScreen.hideStoryBoard();
  }
}
