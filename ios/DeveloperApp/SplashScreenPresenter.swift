import Foundation

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if (rootView != nil) {
      RNBootSplash.initWithStoryboard("LaunchScreen", rootView: rootView!)
    }
  }

  func hide() {
    RNBootSplash.hide(withFade: true);
  }
}
