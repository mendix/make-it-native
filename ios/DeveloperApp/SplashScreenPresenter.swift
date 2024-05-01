import Foundation

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if (rootView != nil) {
      RNSplashScreen.showStoryBoard("SplashScreenA", rootView: rootView!)
    }
  }

  func hide() {
    RNSplashScreen.hideStoryBoard();
  }
}
