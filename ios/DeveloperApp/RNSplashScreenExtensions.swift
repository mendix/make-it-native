import Foundation

extension RNSplashScreen {
  static var loadingView: UIView? = nil;

  static func showStoryBoard(_ name: String, rootView: UIView) {
    if (loadingView == nil) {
      let board = UIStoryboard.init(name: name, bundle: Bundle.main);
      let viewController = board.instantiateInitialViewController();
      loadingView = viewController!.view;
      loadingView!.frame = UIScreen.main.bounds;
    }
    rootView.addSubview(loadingView!);
  }

  static func hideStoryBoard() {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
      UIView.animate(withDuration: 0.3, delay: 0, options: .curveEaseIn, animations: {
        loadingView?.alpha = 0.0;
      }, completion: {_ in
        loadingView?.removeFromSuperview();
        loadingView?.alpha = 1.0;
      });
    };
  }
}
