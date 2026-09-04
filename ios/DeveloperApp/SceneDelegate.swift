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
        if let context = connectionOptions.urlContexts.first {
            handle(url: context.url, options: openURLOptions(from: context))
        }
    }

    @objc func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        guard let context = URLContexts.first else {
            return
        }
        handle(url: context.url, options: openURLOptions(from: context))
    }

    @objc func sceneDidEnterBackground(_ scene: UIScene) {
        SessionCookieStore.persist() //iOS does not persist session cookies across app restarts, this helps persisting session cookies to match behaviour with Android
    }

    private func handle(url: URL, options: [UIApplication.OpenURLOptionsKey: Any]) {
        RCTLinkingManager.application(UIApplication.shared, open: url, options: options)
        guard let appUrl = AppPreferences.appUrl, !appUrl.isEmpty, !ReactAppProvider.isReactAppActive() else {
            return
        }
        var launchOptions: [AnyHashable: Any] = options
        launchOptions[UIApplication.LaunchOptionsKey.annotation] = options[UIApplication.OpenURLOptionsKey.annotation] ?? []
        launchOptions[UIApplication.LaunchOptionsKey.url] = url
        launchMendixAppWithOptions(options: launchOptions)
    }

    private func openURLOptions(from context: UIOpenURLContext) -> [UIApplication.OpenURLOptionsKey: Any] {
        var options: [UIApplication.OpenURLOptionsKey: Any] = [.openInPlace: context.options.openInPlace]
        if let sourceApplication = context.options.sourceApplication {
            options[.sourceApplication] = sourceApplication
        }
        if let annotation = context.options.annotation {
            options[.annotation] = annotation
        }
        return options
    }

    private func launchMendixAppWithOptions(options: [AnyHashable: Any] = [:]) {
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
