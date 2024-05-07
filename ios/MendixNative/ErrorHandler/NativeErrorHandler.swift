import Foundation
import UIKit

@objc(NativeErrorHandler)
public class NativeErrorHandler: NSObject {
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
  @objc(handle:stack:)
  func handle(message: String, stackTrace: [[String: Any]]) {
    if let redBox = ReactNative.instance.getBridge()?.redBox {
      redBox.showErrorMessage(message, withStack: stackTrace)
    } else {
      let alert = UIAlertController(title: "Error", message: message, preferredStyle: .alert)
      alert.addAction(UIAlertAction(title: "Close App", style: .default, handler: { _ in
        DispatchQueue.main.async {
          exit(EXIT_SUCCESS)
        }
      }))
      UIApplication.shared.delegate?.window??.rootViewController?.present(alert, animated: true)
    }
    
    NSLog("Received JS exception: %@", message)
  }
}
