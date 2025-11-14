import MendixNative

enum MendixAppEntryType {
  case deeplink
  case regular
  case sampleApp(SampleApp)
  
  var runtimeUrl: URL {
    switch self {
      case .deeplink, .regular:
      return AppUrl.forRuntime(AppPreferences.safeAppUrl)
    case .sampleApp (let sampleApp):
      return AppUrl.forRuntime(sampleApp.runtimeUrl)
    }
  }
  
  var bundleUrl: URL {
    switch self {
    case .deeplink:
      return AppUrl.forBundle(
          AppPreferences.safeAppUrl,
          port: AppPreferences.remoteDebuggingPackagerPort,
          isDebuggingRemotely: AppPreferences.remoteDebuggingEnabled,
          isDevModeEnabled: AppPreferences.devModeEnabled
      )
    case .regular:
      return AppUrl.forBundle(
          AppPreferences.safeAppUrl,
          port: AppPreferences.remoteDebuggingPackagerPort,
          isDebuggingRemotely: AppPreferences.remoteDebuggingEnabled,
          isDevModeEnabled: AppPreferences.devModeEnabled
      )
    case .sampleApp (let sampleApp):
      if let url = Bundle.main.url(forResource: "Bundles/\(sampleApp.id)/index", withExtension: "bundle") {
        return url
      }
      return AppUrl.forBundle(
        sampleApp.runtimeUrl,
        port: AppPreferences.remoteDebuggingPackagerPort,
        isDebuggingRemotely: false,
        isDevModeEnabled: false
      )
    }
  }
  
  var isDeveloperApp: Bool {
    switch self {
    case .deeplink, .regular:
      return true
    case .sampleApp:
      return false
    }
  }
  
  var splashScreenPresenter: SplashScreenPresenter? {
    switch self {
      case .deeplink, .sampleApp:
      return nil
    case .regular:
      return SplashScreenPresenter()
    }
  }
  
  var enableThreeFingerGestures: Bool {
    switch self {
    case .deeplink, .regular:
      return false
    case .sampleApp:
      return true
    }
  }
  
  var reactLoadingStoryboard: UIStoryboard {
    return .launchScreen
  }
  
  var identifier: String? {
    switch self {
    case .deeplink, .regular:
      return nil
    case .sampleApp (let sampleApp):
      return sampleApp.id
    }
  }
  
  var warningsFilter: WarningsFilter {
    switch self {
    case .deeplink, .regular:
      return getWarningFilterValue()
    case .sampleApp:
      return .none
    }
  }
  
  var clearDataAtLaunch: Bool {
    switch self {
    case .regular:
      return AppState.shared.clearCache
    case .deeplink:
      return false
    case .sampleApp:
      return true
    }
  }
  
  var mendixApp: MendixApp {
    var entry: MendixAppEntryType
    switch self {
    case .deeplink:
      entry = .deeplink
    case .regular:
      entry = .regular
    case .sampleApp (let sampleApp):
      entry = .sampleApp(sampleApp)
    }
    return MendixApp(
      identifier: entry.identifier,
      bundleUrl: entry.bundleUrl,
      runtimeUrl: entry.runtimeUrl,
      warningsFilter: entry.warningsFilter,
      isDeveloperApp: entry.isDeveloperApp,
      clearDataAtLaunch: entry.clearDataAtLaunch,
      splashScreenPresenter: entry.splashScreenPresenter,
      reactLoading: entry.reactLoadingStoryboard,
      enableThreeFingerGestures: entry.enableThreeFingerGestures
    )
  }
}
