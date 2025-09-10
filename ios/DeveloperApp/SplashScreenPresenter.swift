import Foundation

class SplashScreenPresenter: SplashScreenPresenterProtocol  {
  func show(_ rootView: UIView?) {
    if (rootView != nil) {
      RNBootSplash.showStoryBoard("LaunchScreen", rootView: rootView!)
    }
  }

  func hide() {
    // RNBootSplash automatically handles hiding when the React Native app is ready
    // No explicit hide call needed with the new API
    RNBootSplash.hideStoryBoard()
  }
}
