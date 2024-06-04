import UIKit
import AVFoundation
import StoreKit
import SwiftUI

class HomeViewController: UIViewController {
    private var errorShown = false
    private var clearCache: Bool = false
    private var developerMode: Bool = false

    private var uiState: HomeUIState = .idle {
        didSet {
            AppState.shared.uiState = uiState

            if (oldValue != uiState) {
                DispatchQueue.main.async {
                    self.updateUIState()
                }
            }
        }
    }

    private func updateUIState() {
        switch uiState {
        case .idle:
            break
        case .loading:
            break
        case .error, .deprecatedRuntime, .updateAvailable, .runtimeNotRunning, .packagerNotRunning:
            self.performSegue(withIdentifier: "ConnectionError", sender: nil)
            break
        }
    }

    private func setUIState(state: HomeUIState) {
        DispatchQueue.main.async {
            self.uiState = state
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad();

        setupSwiftUIScreen()
        triggerLocalNetworkPrivacyAlert()
    }

    private func setupSwiftUIScreen(){
        // Set up Swift UI screen.
        let homeUI = UIHostingController(rootView: HomeView(
            // Pass variables to Home Swift UI View.
            launchOnTap: {
                self.developerMode = AppState.shared.developerMode
                self.clearCache = AppState.shared.clearCache
                self.textFieldReturnAction(AppState.shared.url.lowercased())
            }
        ))

        view.addSubview(homeUI.view)

        // Adjust frame constraints.
        homeUI.view.translatesAutoresizingMaskIntoConstraints = false
        let constraints = [
            homeUI.view.topAnchor.constraint(equalTo: view.topAnchor),
            homeUI.view.leftAnchor.constraint(equalTo: view.leftAnchor),
            homeUI.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            homeUI.view.rightAnchor.constraint(equalTo: view.rightAnchor)
        ]

        NSLayoutConstraint.activate(constraints)
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

    private func launchApp(_ url: String) {
        HistoryStore().saveHistoryItem(historyItem: HistoryItem(url:url))
        DispatchQueue.main.async {
            AppPreferences.setAppUrl(url)
            AppPreferences.remoteDebugging(false)
            AppPreferences.devMode(self.developerMode)
            self.performSegue(withIdentifier: "OpenMendixApp", sender: nil)
        }
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.destination is MendixAppViewController {
            let url = AppUrl.forBundle(
                AppPreferences.getAppUrl(),
                port: AppPreferences.getRemoteDebuggingPackagerPort(),
                isDebuggingRemotely: AppPreferences.remoteDebuggingEnabled(),
                isDevModeEnabled: AppPreferences.devModeEnabled())

            let runtimeUrl: URL = AppUrl.forRuntime(AppPreferences.getAppUrl())!

            ReactNative.instance.setup(MendixApp(nil, bundleUrl: url!, runtimeUrl: runtimeUrl, warningsFilter: getWarningFilterValue(), isDeveloperApp: true, clearDataAtLaunch: self.clearCache, reactLoading: UIStoryboard(name: "LaunchScreen", bundle: nil)), launchOptions: nil)
        } else if segue.destination is ConnectionErrorViewController {
            let viewController = segue.destination as! ConnectionErrorViewController
            switch uiState {
            case .updateAvailable:
                viewController.model = getVersionErrorModel()
                break

                // Old Studio Pro Version
            case .deprecatedRuntime:
                viewController.model = getVersionErrorModel()
                break

                // Runtime Not Running
            case let .runtimeNotRunning(url):
                viewController.model = getRuntimeErrorModel(url: url)
                break

                // Metro Bundle Not Running
            case let .packagerNotRunning(url):
                viewController.model = getMetroErrorModel(url: url)
                break

            case let .error(message):
                viewController.model = getGeneralErrorModel(message: message)
                break

            default:
                print("Unknown state")
            }
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

    private func getVersionErrorModel()->ConnectErrorModel{
        return ConnectErrorModel(
            title: "We've encountered a version issue.",
            body: "It seems you're trying to connect to a Mendix application that is not supported by this version of the app.",
            instructions: "Please refer to our documentation to learn how to get the correct version of this app for your project.",
            primaryBtnTitle: "Make it Native Versions Guide",
            primaryBtnAction: { () in
                if let url = getUrlFromBundle("HowToMiNVersions") {
                    navigateTo(url)
                }
            },
            secondaryBtnTitle: "Custom Developer App Guide",
            secondaryBtnAction: { () in
                if let url = getUrlFromBundle("HowToDevApps") {
                    navigateTo(url)
                }
            },
            uiState: .updateAvailable
        )
    }

    private func getRuntimeErrorModel(url:String)->ConnectErrorModel{
        let port = AppPreferences.getRemoteDebuggingPackagerPort()
        let metroUrl = AppUrl.forPackagerStatus(url, port: port).absoluteString
        let runtimeUrl = AppUrl.forRuntime(url).absoluteString

        return ConnectErrorModel(
            title: "We've encountered a connection issue.",
            body: "Runtime is not running at: \n\(runtimeUrl)",
            instructions: "Please try the followings \n\n1.   Check port \(port) is accessible \n\n2.   Check native profile is used \n\n3.   Verify that metro is running \n\t(might need to restart Windows)",
            primaryBtnTitle: "Troubleshooting",
            primaryBtnAction: { () in
                if let url = getUrlFromBundle("TroubleShootingUrl") {
                    navigateTo(url)
                }
            },
            secondaryBtnTitle: "Retry",
            secondaryBtnAction: { () in
                self.dismiss(animated: true, completion: nil)
                self.textFieldReturnAction(url)
            },
            uiState: .runtimeNotRunning(url: url),
            metroUrl: metroUrl,
            runtimeUrl: runtimeUrl
        )
    }

    private func getMetroErrorModel(url:String)->ConnectErrorModel{
        let port = AppPreferences.getRemoteDebuggingPackagerPort()
        let metroUrl = AppUrl.forPackagerStatus(url, port: port).absoluteString
        let runtimeUrl = AppUrl.forRuntime(url).absoluteString

        return ConnectErrorModel(
            title: "We've encountered a connection issue.",
            body: "Metro Bundler is not running at: \n\(metroUrl)",
            instructions: "Please try the followings \n\n1.   Check app is running in Studio Pro. \n\n2.   Check IP is correct and accessible \n\n3.   Check port \(port) are accessible",
            primaryBtnTitle: "Troubleshooting",
            primaryBtnAction: { () in
                if let url = getUrlFromBundle("TroubleShootingUrl") {
                    navigateTo(url)
                }
            },
            secondaryBtnTitle: "Retry",
            secondaryBtnAction: { () in
                self.dismiss(animated: true, completion: nil)
                self.textFieldReturnAction(AppUrl.forRuntime(url)!.absoluteString)
            },
            uiState: .packagerNotRunning(url: url),
            metroUrl: metroUrl,
            runtimeUrl: runtimeUrl
        )
    }

    private func getGeneralErrorModel(message:String)->ConnectErrorModel{
        return ConnectErrorModel(
            title: "We've encountered an issue.",
            body: "It seems that there is an error in the app that is preventing it from functioning properly. ",
            instructions: "Please take a look at the options below to tackle the problem and get the app back on track.",
            primaryBtnTitle: "Troubleshooting",
            primaryBtnAction: { () in
                if let url = getUrlFromBundle("TroubleShootingUrl") {
                    navigateTo(url)
                }
            },
            secondaryBtnTitle: "Retry",
            secondaryBtnAction: { () in
                self.dismiss(animated: true, completion: nil)
                self.textFieldReturnAction(AppState.shared.url.lowercased())
            },
            uiState: .error(message: message)
        )
    }

}
