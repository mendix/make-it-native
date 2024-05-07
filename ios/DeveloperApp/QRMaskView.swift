import Foundation
import UIKit

class QRMaskView: UIView {
    private var overlay: CAShapeLayer = {
        var overlay             = CAShapeLayer()
        overlay.backgroundColor = UIColor.clear.cgColor
        overlay.fillColor       = UIColor.clear.cgColor
        overlay.strokeColor     = UIColor.white.cgColor
        overlay.lineWidth       = 3
        overlay.lineDashPattern = [7.0, 7.0]
        overlay.lineDashPhase   = 0

        return overlay
      }()
  
    private var userRect: CGRect?
  
    override init(frame: CGRect) {
        super.init(frame: frame)
      
        setupOverlay()
    }
  
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
      
        setupOverlay()
    }
  
    private func setupOverlay() {
        layer.addSublayer(overlay)
    }
  
    var rectOfInterest: CGRect = CGRect(x: 0.2, y: 0.22, width: 0.6, height: 0.3) {
        didSet {
          setNeedsDisplay()
        }
    }
  
    public override func draw(_ rect: CGRect) {
        let innerRect = CGRect(
          x: rect.width * rectOfInterest.minX,
          y: rect.height * rectOfInterest.minY,
          width: rect.width * rectOfInterest.width,
          height: rect.height * rectOfInterest.height
        )

        overlay.path = UIBezierPath(roundedRect: innerRect, cornerRadius: 5).cgPath
    }
}
