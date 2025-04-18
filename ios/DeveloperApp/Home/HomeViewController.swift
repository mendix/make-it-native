import UIKit
import AVFoundation
import QRCodeReader
import StoreKit

class HomeViewController: UIViewController, QRCodeReaderViewControllerDelegate, UITextFieldDelegate {
    @IBOutlet weak var continueBtn: UIButton!
    @IBOutlet weak var cancelScanBtn: UIButton!
    @IBOutlet weak var headerView: UIView!
    @IBOutlet weak var qrView: UIView!
    @IBOutlet weak var urlTextField: UITextField!
    @IBOutlet weak var clearDataSwitch: UISwitch!
    @IBOutlet weak var devModeSwitch: UISwitch!
    @IBOutlet weak var errorContainerView: UIView!
    @IBOutlet weak var errorLabel: UILabel!

    private var errorShown = false
    private var urlText: String?

    private var keyboardShowTask: DispatchWorkItem?
    private let KEYBOARD_ANIMATION_DURATION_IN_S = 0.25
    private let KEYBOARD_SHOW_ANIMATION_DEBOUNCE_TIME_IN_S = 0.01
    
    private let qrMask = QRMaskView()

    lazy var qrReaderVC: QRCodeReaderViewController = {
        let builder = QRCodeReaderViewControllerBuilder {
            $0.reader = QRCodeReader(metadataObjectTypes: [.qr], captureDevicePosition: .back)
            $0.showTorchButton        = false
            $0.showSwitchCameraButton = false
            $0.showCancelButton       = false
            $0.showOverlayView        = false
        }

        return QRCodeReaderViewController(builder: builder)
    }()

    private var uiState: State = .idle {
        didSet {
            if (oldValue != uiState) {
                DispatchQueue.main.async {
                    self.updateUIState()
                }
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad();

        self.view.insetsLayoutMarginsFromSafeArea = false
        continueBtn.layer.cornerRadius = 8.0
        qrView.addSubview(qrReaderVC.view)
        // Setup QRCode Mask
        qrView.addSubview(qrMask)
        qrMask.backgroundColor = .clear
        qrMask.clipsToBounds = true
        qrMask.translatesAutoresizingMaskIntoConstraints = false
        qrMask.widthAnchor.constraint(equalToConstant: view.frame.width).isActive = true
        qrMask.heightAnchor.constraint(equalToConstant: view.frame.height).isActive = true
        // Hides the QRCodeView layer
        qrView.layer.opacity = 0.0
        cancelScanBtn.layer.opacity = 0.0
        qrReaderVC.delegate = self

        errorContainerView.layer.cornerRadius = 8.0

        urlTextField.delegate = self
        urlTextField.layer.cornerRadius = 8.0
        if let placeHolderText = urlTextField.placeholder {
            urlTextField.attributedPlaceholder = NSAttributedString(string: placeHolderText, attributes: [NSAttributedStringKey.foregroundColor: UIColor.lightGray])
        }

        triggerLocalNetworkPrivacyAlert()
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        urlTextField.text = AppPreferences.getAppUrl()
        clearDataSwitch.setOn(true, animated: false)
        registerNotificationObservers()
    }

    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        unregisterNotificationObservers()
    }

    @IBAction func cancelScanAction(_ sender: Any) {
        cancelScanning()
    }

    @IBAction func hideKeyboardAction(_ sender: Any) {
        urlTextField.resignFirstResponder()
    }

    @IBAction func continueAction(_ sender: Any) {
        let _ = textFieldShouldReturn(urlTextField)
    }

    @IBAction func scanAction(_ sender: Any) {
        // TODO: (jogboms) add some animation
        requestCameraAccess() {
            self.view.endEditing(true)
            self.headerView.layer.opacity = 0.0
            self.qrView.layer.opacity = 1.0
            self.cancelScanBtn.layer.opacity = 1.0
            self.qrReaderVC.startScanning()
        }
    }

    func cancelScanning() {
        // TODO: (jogboms) add some animation
        headerView.layer.opacity = 1.0
        qrView.layer.opacity = 0.0
        cancelScanBtn.layer.opacity = 0.0
        qrReaderVC.stopScanning()
    }
  
    func textFieldReturnAction(_ urlText: String) {
        if (uiState == .loading) {
          return
        }

        uiState = .loading
        validateMendixUrl(urlText) { (running) in
            guard running, AppUrl.isValid(urlText) else {
                self.uiState = .runtimeNotRunning(url: urlText)
                return
            }
            self.urlTextField.resignFirstResponder()

            self.isSupportedMendixVersion(urlString: urlText) {
                supported in
                if (!supported) {
                    return
                }
                self.uiState = .idle
                self.launchApp(urlText)
            }
        }
    }

    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textFieldReturnAction(textField.text ?? "")
        return true
    }

    private func launchApp(_ url: String) {
        DispatchQueue.main.async {
            AppPreferences.setAppUrl(url)
            AppPreferences.remoteDebugging(false)
            AppPreferences.devMode(self.devModeSwitch.isOn)
            self.performSegue(withIdentifier: "OpenMendixApp", sender: nil)
        }
    }

    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        uiState = .loading
        
        let url = result.value

        validateMendixUrl(url) { (running) in
            guard running, AppUrl.isValid(url) else {
                reader.startScanning()
                self.uiState = .runtimeNotRunning(url: url)
                return
            }

            self.urlTextField.text = url
            self.isSupportedMendixVersion(urlString: url) { (supported) in
                if (!supported) {
                    return
                }
                self.uiState = .idle
                self.cancelScanning()
                self.launchApp(url)
            }
        }
    }

    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        cancelScanning()
    }

    private func registerNotificationObservers() {
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow), name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide), name: .UIKeyboardWillHide, object: nil)
    }

    private func unregisterNotificationObservers() {
        NotificationCenter.default.removeObserver(self, name: .UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: .UIKeyboardWillHide, object: nil)
    }

    @objc func keyboardWillShow(_ notification: Notification) {
        self.keyboardShowTask?.cancel()
        self.keyboardShowTask = DispatchWorkItem {
            if let keyboardRect = notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? CGRect {
                let duration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? Double
                self.view.frame.size.height = UIScreen.main.bounds.height - keyboardRect.height
                if (self.qrView.layer.opacity != 0) {
                    self.cancelScanning()
                }
                UIView.animate(withDuration: (duration ?? self.KEYBOARD_ANIMATION_DURATION_IN_S), animations: {
                    self.view.layoutIfNeeded()
                })
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + KEYBOARD_SHOW_ANIMATION_DEBOUNCE_TIME_IN_S, execute: self.keyboardShowTask!)
    }

    @objc func keyboardWillHide(_ notification: Notification) {
        let duration = notification.userInfo?[UIKeyboardAnimationDurationUserInfoKey] as? Double
        view.frame.size.height = UIScreen.main.bounds.height - (self.tabBarController?.tabBar.frame.height ?? 0.0)
        UIView.animate(withDuration: duration ?? KEYBOARD_ANIMATION_DURATION_IN_S, animations: {
            self.view.layoutIfNeeded()
        })
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination is MendixAppViewController {
            let url = AppUrl.forBundle(
              AppPreferences.getAppUrl(),
              port: AppPreferences.getRemoteDebuggingPackagerPort(),
              isDebuggingRemotely: AppPreferences.remoteDebuggingEnabled(),
              isDevModeEnabled: AppPreferences.devModeEnabled())

            let runtimeUrl: URL = AppUrl.forRuntime(AppPreferences.getAppUrl())!
            
            let mxApp = MendixApp(nil, bundleUrl: url!, runtimeUrl: runtimeUrl, warningsFilter: getWarningFilterValue(), isDeveloperApp: true, clearDataAtLaunch: clearDataSwitch.isOn, reactLoading: UIStoryboard(name: "LaunchScreen", bundle: nil))
            mxApp.splashScreenPresenter = SplashScreenPresenter()
            ReactNative.instance.setup(mxApp, launchOptions: nil)
        } else if segue.destination is ConnectionErrorViewController {
            let viewController = segue.destination as! ConnectionErrorViewController
            switch uiState {
            case .updateAvailable:
                viewController.model = ConnectErrorModel(title: "Check for update.", body: "You are using an outdated version of the Make It Native app.", primaryBtnTitle: "UPDATE APP", primaryBtnAction: { () in
                    self.dismiss(animated: true, completion: nil)
                    let storeViewController = SKStoreProductViewController()
                    storeViewController.loadProduct(withParameters: [SKStoreProductParameterITunesItemIdentifier: 1334081181], completionBlock: nil)
                    self.present(storeViewController, animated: true, completion: nil)
                })
                break
            case .deprecatedRuntime:
                viewController.model = ConnectErrorModel(title: "Version not supported.", body: "You are trying to connect to an older version of Mendix Studio Pro which is not supported.", primaryBtnTitle: "MORE INFO", primaryBtnAction: { () in
                    if let url = URL(string: "https://docs.mendix.com/refguide/getting-the-make-it-native-app#direct-links") {
                        UIApplication.shared.open(url)
                    }
                })
                break
            case let .runtimeNotRunning(url):
                viewController.model = ConnectErrorModel(title: "Connection Error.", body: "We cannot detect Mendix Studio Pro. Please make sure that Mendix Studio Pro is running on your local machine and accessible to your current device using the provided URL.", primaryBtnTitle: "TEST CONNECTION", primaryBtnAction: { () in
                    if let runtimeUrl: URL = AppUrl.forRuntime(url) {
                        UIApplication.shared.open(runtimeUrl)
                    }
                }, secondaryBtnTitle: "RETRY", secondaryBtnAction: { () in
                    self.dismiss(animated: true, completion: nil)
                    self.textFieldReturnAction(url)
                })
                break
            case let .packagerNotRunning(url):
                viewController.model = ConnectErrorModel(title: "Packager Connection Error.", body: "We cannot detect the Native Packager running. Please make sure that your project includes a Native Navigation Profile and then restart your runtime. If it persists, check your deployments logs for errors.", primaryBtnTitle: "TEST CONNECTION", primaryBtnAction: { () in
                    if let packagerStatusUrl: URL = AppUrl.forPackagerStatus(url, port: AppPreferences.getRemoteDebuggingPackagerPort()) {
                        UIApplication.shared.open(packagerStatusUrl)
                    }
                }, secondaryBtnTitle: "RETRY", secondaryBtnAction: { () in
                    self.dismiss(animated: true, completion: nil)
                    self.textFieldReturnAction(AppUrl.forRuntime(url)!.absoluteString)
                })
                break
            default:
                print("Unknown state")
            }
        }
    }

    private enum State: Equatable {
        case idle
        case loading
        case error(message: String)
        case deprecatedRuntime
        case updateAvailable
        case runtimeNotRunning(url: String)
        case packagerNotRunning(url: String)
    }

    private func updateUIState() {
        switch uiState {
        case .idle:
            urlTextField.layer.opacity = 1
            break
        case .loading:
            urlTextField.layer.opacity = 0.5
            break
        case .error(let message):
            urlTextField.layer.opacity = 1
            showErrorDialog(error: message)
            break
        case .deprecatedRuntime, .updateAvailable, .runtimeNotRunning, .packagerNotRunning:
            urlTextField.layer.opacity = 1
            self.performSegue(withIdentifier: "ConnectionError", sender: nil)
            break
        }
    }

    private func setUIState(state: State) {
        DispatchQueue.main.async {
            self.uiState = state
        }
    }

    private func isSupportedMendixVersion(urlString: String, cb: @escaping (Bool) -> Void) {
        guard let runtimeUrl = AppUrl.forRuntimeInfo(urlString) else {
            self.uiState = .error(message: "The URL provided does not point to a valid Mendix app.")
            return
        }
      
        let sessionConfiguration = URLSessionConfiguration.ephemeral
        sessionConfiguration.timeoutIntervalForRequest = 3
        URLSession(configuration: sessionConfiguration).dataTask(with: AppUrl.forPackagerStatus(urlString, port: AppPreferences.getRemoteDebuggingPackagerPort())) { (_, __, error) in
            if (error != nil) {
                self.uiState = .packagerNotRunning(url: runtimeUrl.absoluteString)
                return cb(false)
            }
        
            RuntimeInfoProvider.getRuntimeInfo(runtimeUrl) { (response) in
                guard let runtimeInfoResponse = response else {
                    self.uiState = .runtimeNotRunning(url: urlString)
                    return cb(false)
                }

                if (runtimeInfoResponse.status == "INACCESSIBLE") {
                    self.uiState = .runtimeNotRunning(url: urlString)
                    return cb(false)
                }

                if (runtimeInfoResponse.status == "FAILED") {
                    self.uiState = .deprecatedRuntime
                    return cb(false)
                }
                guard let runtimeInfo = runtimeInfoResponse.runtimeInfo, let supportedNativeBinaryVersion =  Bundle.main.object(forInfoDictionaryKey: "Native Binary Version") as? Int else {
                    self.uiState = .deprecatedRuntime
                    return cb(false)
                }
                
                let nativeBinaryVersion = runtimeInfo.nativeBinaryVersion
                if (nativeBinaryVersion == supportedNativeBinaryVersion) {
                    return cb(true)
                } else if (nativeBinaryVersion > supportedNativeBinaryVersion) {
                    self.uiState = .updateAvailable
                    return cb(false)
                } else {
                    self.uiState = .deprecatedRuntime
                    return cb(false)
                }
            }
        }.resume()
    }

    private func validateMendixUrl(_ urlString: String, onCompletion: @escaping (_ valid: Bool) -> Void) {
        if let url = AppUrl.forValidation(urlString) {
            URLValidator.validate(url, onCompletion: onCompletion)
            return
        }

        onCompletion(false)
    }

    private func showErrorDialog(error: String) {
        errorLabel.text = error
        if (errorShown) {
            return
        }
        errorShown = true
        animateErrorDialog(from: 0, to: 1)
        DispatchQueue.main.asyncAfter(deadline: .now() + 6.0) {
            self.animateErrorDialog(from: 1, to: 0)
            self.errorShown = false
            self.setUIState(state: .idle)
        }
    }

    private func animateErrorDialog(from: Float, to: Float) {
        let animation = CABasicAnimation(keyPath: "opacity")
        animation.duration = 0.25
        animation.fromValue = from
        animation.toValue = to
        animation.fillMode = kCAFillModeForwards
        animation.isRemovedOnCompletion = false
        errorContainerView.layer.add(animation, forKey: "opacity")
    }

    private func requestCameraAccess(complete: @escaping () -> Void){
        AVCaptureDevice.requestAccess(for: .video) { success in
            if success {
                DispatchQueue.main.async {
                    complete()
                }
                return
            }

            let alert = UIAlertController(title: "Camera", message: "Camera access is required to use the QR scanner", preferredStyle: .alert)

            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
                UIApplication.shared.open(URL(string: UIApplicationOpenSettingsURLString)!)
            }))

            DispatchQueue.main.async {
                self.present(alert, animated: true)
            }
        }
    }
}
