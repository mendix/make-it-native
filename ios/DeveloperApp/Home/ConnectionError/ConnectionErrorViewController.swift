import UIKit
import SwiftUI

typealias ActionCallback = (() -> ())

struct ConnectErrorModel {
  let title: String
  let body: String
  let instructions:String
  let uiState: HomeUIState
  let primaryBtnTitle: String?
  let primaryBtnAction: ActionCallback?
  let secondaryBtnTitle: String?
  let secondaryBtnAction: ActionCallback?
  let metroUrl: String
  let runtimeUrl: String
  
  init(
    title: String = "",
    body: String = "",
    instructions: String = "",
    primaryBtnTitle: String? = nil,
    primaryBtnAction: ActionCallback? = nil,
    secondaryBtnTitle: String? = nil,
    secondaryBtnAction: ActionCallback? = nil,
    uiState: HomeUIState = .idle,
    metroUrl:String = "",
    runtimeUrl: String = ""
  ) {
    self.title = title
    self.body = body
    self.instructions = instructions
    self.uiState = uiState
    self.primaryBtnTitle = primaryBtnTitle
    self.primaryBtnAction = primaryBtnAction
    self.secondaryBtnTitle = secondaryBtnTitle
    self.secondaryBtnAction = secondaryBtnAction
    self.metroUrl = metroUrl
    self.runtimeUrl = runtimeUrl
  }
}

class ConnectionErrorViewController: UIViewController {
  var model = ConnectErrorModel()
  
  override func viewDidLoad() {
    super.viewDidLoad();
    
    setupSwiftUIScreen()
  }
  
  private func setupSwiftUIScreen(){
    
    // Set up Swift UI screen.
    let troubleShootView = UIHostingController(
      rootView:
        TroubleshootView(
          primaryButtonTitle: model.primaryBtnTitle ?? "",
          secondaryButtonTitle: model.secondaryBtnTitle ?? "",
          title: model.title,
          description: model.body,
          instructions: model.instructions,
          backOnTap: {
            self.dismiss(animated: true, completion: nil)
          },
          primaryButtonOnTap:{
            if let callableAction = self.model.primaryBtnAction {
              DispatchQueue.main.async {
                callableAction()
              }
            }
          },
          secondaryButtonOnTap: {
            if let callableAction = self.model.secondaryBtnAction {
              DispatchQueue.main.async {
                callableAction()
              }
            }
          }
        ))
    
    view.addSubview(troubleShootView.view)
    
    // Adjust frame constraints.
    troubleShootView.view.translatesAutoresizingMaskIntoConstraints = false
    let constraints = [
      troubleShootView.view.topAnchor.constraint(equalTo: view.topAnchor),
      troubleShootView.view.leftAnchor.constraint(equalTo: view.leftAnchor),
      troubleShootView.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
      troubleShootView.view.rightAnchor.constraint(equalTo: view.rightAnchor)
    ]
    
    NSLayoutConstraint.activate(constraints)
  }
}
