import UIKit
import Firebase
import GoogleMaps
import UserNotifications
import React_RCTAppDelegate
import ReactAppDependencyProvider
import MendixNative
import React

@UIApplicationMain
class AppDelegate: ReactAppProvider {
    
    var shouldLaunchLastApp: Bool = false
    var previewingSampleApp: Bool = false
    
    override func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        SessionCookieStore.restore()
        super.setUpProvider()
        super.application(application, didFinishLaunchingWithOptions: launchOptions)
        clearKeychainIfNecessary()
        setUpDevice()
        setUpGoogleMaps()
        setUpPushNotifications(application)
        updateRootViewController(showOnboarding() ? .launchTutorial : .openApp)
        return true
    }
    
    override func application(_ app: UIApplication, open url: URL, options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        RCTLinkingManager.application(app, open: url, options: options)
        guard let appUrl = AppPreferences.appUrl, !appUrl.isEmpty, !ReactAppProvider.isReactAppActive() else {
            return true
        }
        var launchOptions: [AnyHashable: Any] = options
        launchOptions[UIApplication.LaunchOptionsKey.annotation] = options[UIApplication.OpenURLOptionsKey.annotation] ?? []
        launchOptions[UIApplication.LaunchOptionsKey.url] = url
        launchMendixAppWithOptions(options: launchOptions)
        return true
    }
    
    override func applicationDidEnterBackground(_ application: UIApplication) {
        SessionCookieStore.persist()
    }
        
    override func applicationWillTerminate(_ application: UIApplication) {
        SessionCookieStore.persist()
    }
    
    private func launchMendixAppWithOptions(options: [AnyHashable: Any] = [:]) {
        ReactNative.shared.setup(MendixAppEntryType.deeplink.mendixApp, launchOptions: options)
        ReactNative.shared.start()
    }
    
    static func delegateInstance() -> AppDelegate? {
        return UIApplication.shared.delegate as? AppDelegate
    }
}

//Device
extension AppDelegate {
    
    static var orientationLock = UIInterfaceOrientationMask.portrait // By default lock orientation only portrait mode.
    
    override func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return AppDelegate.orientationLock
    }
    
    private func setUpDevice() {
        UIApplication.shared.isIdleTimerDisabled = true
        UIDevice.current.isBatteryMonitoringEnabled = true
    }
}

//RootView
extension AppDelegate {
    private func updateRootViewController(_ storyboard: UIStoryboard) {
        window = MendixReactWindow(frame: UIScreen.main.bounds)
        window.rootViewController = storyboard.instantiateInitialViewController()
        window.makeKeyAndVisible()
        window.overrideUserInterfaceStyle = .light // Force Light Mode
        IQKeyboardManager.shared().isEnabled = false
    }
    
    func changeRootViewToOpenApp() {
        updateRootViewController(.openApp)
    }
}

//GMS
extension AppDelegate {
    private func setUpGoogleMaps() {
        guard let apiKey = Bundle.main.object(forInfoDictionaryKey: "GoogleMapsKey") as? String, !apiKey.isEmpty else {
            GMSServices.provideAPIKey("placeholderApiKey")
            return
        }
        GMSServices.provideAPIKey(apiKey)
    }
}

//Keychain
extension AppDelegate {
    func clearKeychainIfNecessary() {
        if (UserDefaults.standard.bool(forKey: "HAS_RUN_BEFORE") == false) {
            UserDefaults.standard.setValue(true, forKey: "HAS_RUN_BEFORE")
            KeychainHelper.clear()
        }
    }
}

//Firebase + Push Notification
extension AppDelegate: UNUserNotificationCenterDelegate, MessagingDelegate {
    
    private func setUpPushNotifications(_ application: UIApplication) {
        // Required for Remote notifications
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        //Register MiN for remote Notifications
        // For iOS 10+ display notification (sent via APNS)
        UNUserNotificationCenter.current().delegate = self
        
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: {_, _ in })
        application.registerForRemoteNotifications()
    }
    
    //Called when a notification is delivered to a foreground app.
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.sound, .badge, .banner, .list])
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        completionHandler()
    }
    
    func handleMendixNotification(response: UNNotificationResponse) -> Bool {
        let mendixAdChannelId = "MENDIX_AD_CAMPAIGN_CHANNEL"
        let userInfo = response.notification.request.content.userInfo
        let id = (userInfo["id"] as? String)?.trimmingCharacters(in: .whitespaces)
        let action = (userInfo["action"] as? String)?.trimmingCharacters(in: .whitespaces)
        let url = (userInfo["url"] as? String)?.trimmingCharacters(in: .whitespaces)
        if (id == mendixAdChannelId && action != nil) {
            if action == "launch-url", url != nil, let webUrl = URL(string: url!) {
                UIApplication.shared.open(webUrl)
                return true
            }
        }
        return false
    }
    
#if DEBUG
    override func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("APNs token retrieved: \(deviceToken)")
        Messaging.messaging().apnsToken = deviceToken
        addDevFirebaseTokenReader()
    }

    func addDevFirebaseTokenReader() {
        Messaging.messaging().token { token, error in
            if let error = error {
                print("Error fetching FCM registration token: \(error)")
            } else if let token = token {
                print("FCM registration token: \(token)")
            }
        }
    }
#endif
}
