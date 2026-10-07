import UIKit
import MendixNative

class SceneDelegate: ReactAppProvider {

    var shouldLaunchLastApp: Bool = false
    var previewingSampleApp: Bool = false

    override func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        super.scene(scene, willConnectTo: session, options: connectionOptions)
        setUpProvider()
        updateRootViewController(showOnboarding() ? .launchTutorial : .openApp)
        // A cold start triggered by a deep link delivers the URL here instead of through scene(_:openURLContexts:).
        if !connectionOptions.urlContexts.isEmpty {
            launchMendixAppWithOptions(options: ReactAppProvider.launchOptions(from: connectionOptions))
        }
    }

    override func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        handleURLContexts(URLContexts) { [weak self] launchOptions in
            self?.launchMendixAppWithOptions(options: launchOptions)
        }
    }

    @objc func sceneDidEnterBackground(_ scene: UIScene) {
        SessionCookieStore.persist() //iOS does not persist session cookies across app restarts, this helps persisting session cookies to match behaviour with Android
    }

    private func launchMendixAppWithOptions(options: [AnyHashable: Any]) {
        guard let appUrl = AppPreferences.appUrl, !appUrl.isEmpty else {
            return
        }
        ReactNative.shared.setup(MendixAppEntryType.deeplink.mendixApp, launchOptions: options)
        ReactNative.shared.start()
    }

    static func delegateInstance() -> SceneDelegate? {
        if let provider = ReactAppProvider.shared() as? SceneDelegate {
            return provider
        }
        return UIApplication.shared.connectedScenes.compactMap { $0.delegate as? SceneDelegate }.first
    }
}

//RootView
extension SceneDelegate {
    private func updateRootViewController(_ storyboard: UIStoryboard) {
        guard let rootViewController = storyboard.instantiateInitialViewController() else {
            return
        }
        changeRoot(to: rootViewController)
        window?.overrideUserInterfaceStyle = .light
        IQKeyboardManager.shared().isEnabled = false
    }

    func changeRootViewToOpenApp() {
        updateRootViewController(.openApp)
    }
}
