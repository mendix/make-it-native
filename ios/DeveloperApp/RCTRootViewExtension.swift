import Foundation

extension RCTRootView {
  override open func willMove(toWindow newWindow: UIWindow?) {
    super.willMove(toWindow: newWindow)
    backgroundColor = UIColor.init(white: CGFloat(0.0), alpha: CGFloat(0.0))
  }
}
