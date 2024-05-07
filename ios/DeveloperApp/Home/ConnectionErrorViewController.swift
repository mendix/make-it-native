import UIKit

typealias ActionCallback = (() -> ())

struct ConnectErrorModel {
  let title: String
  let body: String
  let primaryBtnTitle: String?
  let primaryBtnAction: ActionCallback?
  let secondaryBtnTitle: String?
  let secondaryBtnAction: ActionCallback?
  
  init(title: String, body: String, primaryBtnTitle: String? = nil, primaryBtnAction: ActionCallback? = nil, secondaryBtnTitle: String? = nil, secondaryBtnAction: ActionCallback? = nil) {
    self.title = title
    self.body = body
    self.primaryBtnTitle = primaryBtnTitle
    self.primaryBtnAction = primaryBtnAction
    self.secondaryBtnTitle = secondaryBtnTitle
    self.secondaryBtnAction = secondaryBtnAction
  }
}

class ConnectionErrorViewController: UIViewController {
  @IBOutlet weak var goBackBtn: UIButton!
  @IBOutlet weak var titleLabel: UILabel!
  @IBOutlet weak var descriptionLabel: UILabel!
  @IBOutlet weak var primaryBtn: UIButton!
  @IBOutlet weak var secondaryBtn: UIButton!

  var model = ConnectErrorModel(title: "", body: "")

  override func viewDidLoad() {
    super.viewDidLoad();

    if let primaryBtnTitle = model.primaryBtnTitle {
      primaryBtn.setTitle(primaryBtnTitle, for: .normal)
    } else {
      primaryBtn.isHidden = true
    }
    if let secondaryBtnTitle = model.secondaryBtnTitle {
      secondaryBtn.setTitle(secondaryBtnTitle, for: .normal)
    } else {
      secondaryBtn.isHidden = true
    }

    goBackBtn.layer.cornerRadius = 8.0
    titleLabel.text = model.title
    descriptionLabel.text = model.body
  }

  @IBAction func goBackAction(_ sender: Any) {
    self.dismiss(animated: true, completion: nil)
  }

  @IBAction func primaryBtnAction(_ sender: Any) {
    if let callableAction = model.primaryBtnAction {
      DispatchQueue.main.async {
        callableAction()
      }
    }
  }

  @IBAction func secondaryBtnAction(_ sender: Any) {
    if let callableAction = model.secondaryBtnAction {
      DispatchQueue.main.async {
        callableAction()
      }
    }
  }
}
