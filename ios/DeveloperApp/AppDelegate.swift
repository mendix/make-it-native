import UIKit
import Firebase
import GoogleMaps
import UserNotifications
import React

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    var window: UIWindow?
    var shouldLaunchLastApp: Bool = false
    var previewingSampleApp: Bool = false
    
    // By default lock orientation only portrait mode.
    static var orientationLock = UIInterfaceOrientationMask.portrait
    
    func application(_ application: UIApplication, supportedInterfaceOrientationsFor window: UIWindow?) -> UIInterfaceOrientationMask {
        return AppDelegate.orientationLock
    }
    
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        SessionCookieStore.restore()
        clearKeychainIfNecessary()
        
        UIApplication.shared.isIdleTimerDisabled = true
        
        UIDevice.current.isBatteryMonitoringEnabled = true
        
        let googleApiKey = Bundle.main.object(forInfoDictionaryKey: "GoogleMapsKey") as! String
        GMSServices.provideAPIKey(googleApiKey != "" ? googleApiKey : "placeholderApiKey")
        
        // Required for Remote notifications
        if FirebaseApp.app() == nil {
            FirebaseApp.configure()
        }
        //Register MiN for remote Notifications
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
            UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
        
        let onboardingShown = !showOnboarding() // showOnboardin return true, if onboarding has not shown up.
        let storyboard: UIStoryboard?
        
        // If the onboarding was shown, do not show.
        if (!onboardingShown) {
            storyboard = UIStoryboard(name: "LaunchTutorial", bundle: nil)
        } else {
            storyboard = UIStoryboard(name: "OpenApp", bundle: nil)
        }
        
        let rootViewController = storyboard!.instantiateInitialViewController()
        self.window = MendixReactWindow(frame: UIScreen.main.bounds)
        self.window?.rootViewController = rootViewController
        self.window?.makeKeyAndVisible()
        
        // Force Light Mode
        self.window?.overrideUserInterfaceStyle = .light
        
        IQKeyboardManager.shared().isEnabled = false;
        
        return true
    }
    
    func changeRootViewToOpenApp(){
        let storyboard: UIStoryboard? = UIStoryboard(name: "OpenApp", bundle: nil)
        
        let rootViewController = storyboard!.instantiateInitialViewController()
        self.window = MendixReactWindow(frame: UIScreen.main.bounds)
        self.window?.rootViewController = rootViewController
        self.window?.makeKeyAndVisible()
        
        // Force Light Mode
        self.window?.overrideUserInterfaceStyle = .light
        
        IQKeyboardManager.shared().isEnabled = false;
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
        RCTLinkingManager.application(app, open: url, options: options)
        guard let appUrl = AppPreferences.getAppUrl(), !appUrl.isEmpty, !ReactNative.instance.isActive() else {
            return true
        }
        var launchOptions: [AnyHashable: Any] = options
        launchOptions[UIApplicationLaunchOptionsKey.annotation] = options[UIApplicationOpenURLOptionsKey.annotation] ?? []
        launchOptions[UIApplicationLaunchOptionsKey.url] = url
        launchMendixAppWithOptions(options: launchOptions)
        return true
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        SessionCookieStore.persist()
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        SessionCookieStore.persist()
    }
    
    //Called when a notification is delivered to a foreground app.
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        completionHandler([.sound, .alert, .badge])
    }
    
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        if (!handleMendixNotification(response: response)) {
            RNCPushNotificationIOS.didReceive(response)
        }
        completionHandler()
    }
    
    private func launchMendixAppWithOptions(options: [AnyHashable: Any] = [:]) {
        let url = AppUrl.forBundle(
            AppPreferences.getAppUrl(),
            port: AppPreferences.getRemoteDebuggingPackagerPort(),
            isDebuggingRemotely: AppPreferences.remoteDebuggingEnabled(),
            isDevModeEnabled: AppPreferences.devModeEnabled())
        let runtimeUrl: URL = AppUrl.forRuntime(AppPreferences.getAppUrl())!
        let mendixApp = MendixApp(nil, bundleUrl: url!, runtimeUrl: runtimeUrl, warningsFilter: getWarningFilterValue(), isDeveloperApp: true, clearDataAtLaunch: false, reactLoading: UIStoryboard(name: "LaunchScreen", bundle: nil))
        ReactNative.instance.setup(mendixApp, launchOptions: options)
        ReactNative.instance.start()
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
    
    func clearKeychainIfNecessary() {
        if (UserDefaults.standard.bool(forKey: "HAS_RUN_BEFORE") == false) {
            UserDefaults.standard.setValue(true, forKey: "HAS_RUN_BEFORE")
            ReactNative.clearKeychain()
        }
    }
    
#if DEBUG
    func application(_ application: UIApplication,
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
