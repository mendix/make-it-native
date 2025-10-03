import UIKit
import SwiftUI
import MendixNative

class SampleAppCollectionViewController: UIViewController {
  private let config = SampleAppsConfig()
  private var sampleApps = [SampleApp]()
  private var currentSelectedSampleAppIndex = 1
  
  var topbarHeight: CGFloat {
      return (view.window?.windowScene?.statusBarManager?.statusBarFrame.height ?? 0.0) +
          (self.navigationController?.navigationBar.frame.height ?? 0.0)
  }
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    sampleApps = config.read()
    setUpSampleAppsUIView()
  }
  
  func setUpSampleAppsUIView(){
    let sampleAppsUI = UIHostingController(rootView: SampleAppsSwiftUIView(onTap: { app in
      self.onTap(app: app)
    }))
    
    view.addSubview(sampleAppsUI.view)
    sampleAppsUI.view.translatesAutoresizingMaskIntoConstraints = false
    
    let constraints = [
      sampleAppsUI.view.topAnchor.constraint(equalTo: view.topAnchor),
      sampleAppsUI.view.leftAnchor.constraint(equalTo: view.leftAnchor),
      sampleAppsUI.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
      sampleAppsUI.view.rightAnchor.constraint(equalTo: view.rightAnchor)
    ]

    NSLayoutConstraint.activate(constraints)
  }
  
  func onTap(app: SampleApp) {
    let appDelegate = (UIApplication.shared.delegate as! AppDelegate)
    appDelegate.previewingSampleApp = true
    
    for i in 0..<sampleApps.count {
      if sampleApps[i].id == app.id {
        currentSelectedSampleAppIndex = i
      }
    }
    
    performSegue(withIdentifier: "OpenMendixApp", sender: self)
  }
  
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if segue.destination is MendixAppViewController {
      let sampleApp = sampleApps[currentSelectedSampleAppIndex]
      AppPreferences.devModeEnabled = false
      ReactNative.instance.setup(MendixAppEntryType.sampleApp(sampleApp).mendixApp, launchOptions: nil)
    }
  }
  
}
