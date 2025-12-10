import Foundation

extension RCTRootView {
  override open func willMove(toWindow newWindow: UIWindow?) {
    super.willMove(toWindow: newWindow)
    backgroundColor = UIColor(white: 0.0, alpha: 0.0)
  }
}
