import UIKit

@IBDesignable
class PaddingTextField: UITextField {
  @IBInspectable var paddingTop: CGFloat = 0
  @IBInspectable var paddingRight: CGFloat = 0
  @IBInspectable var paddingBottom: CGFloat = 0
  @IBInspectable var paddingLeft: CGFloat = 0
  
  override func textRect(forBounds bounds: CGRect) -> CGRect {
    return bounds.inset(by: createPaddingInsets())
  }
  
  override func placeholderRect(forBounds bounds: CGRect) -> CGRect {
    return bounds.inset(by: createPaddingInsets())
  }
  
  override func editingRect(forBounds bounds: CGRect) -> CGRect {
    return bounds.inset(by: createPaddingInsets())
  }
  
  private func createPaddingInsets() -> UIEdgeInsets {
    return UIEdgeInsets(top: paddingTop, left: paddingLeft, bottom: paddingBottom, right: paddingRight)
  }
}
