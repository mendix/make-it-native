package com.mendix.developerapp

import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
import com.facebook.react.internal.featureflags.ReactNativeFeatureFlags
import com.facebook.react.internal.featureflags.ReactNativeNewArchitectureFeatureFlagsDefaults
import com.mendix.developerapp.loading.BundleDownloadListenerHolder
import com.mendix.developerapp.sampelapps.SampleAppsManager
import com.mendix.developerapp.splashscreen.SplashScreenPresenter
import com.mendix.developerapp.util.imageBackground
import com.mendix.developerapp.util.pickRandomBackground
import com.mendix.developerapp.util.splashBackground
import com.mendix.mendixnative.JSBundleFileProvider
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter

class MainApplication : BaseApplication() {

    companion object {
        private var backgroundImage = pickRandomBackground()
        var backgroundImagePath = imageBackground(backgroundImage)
        var splashImagePath = splashBackground(backgroundImage)
    }

    override fun onCreate() {
        // MendixReactApplication.onCreate() runs SoLoader.init() and then
        // DefaultNewArchitectureEntryPoint.load(), which itself calls
        // ReactNativeFeatureFlags.override(...) exactly once. So by the time we
        // return from super.onCreate() the native lib is loaded AND the flags have
        // already been overridden once.
        super.onCreate()

        // Enable the React Native DevTools (Fusebox) CDP backend in release builds.
        // In release, InspectorFlags reads ReactNativeFeatureFlags.fuseboxEnabledRelease()
        // (defaults to false), which bakes a "legacy" vs "fusebox" inspector target id into
        // the device registration. Without this, the dev menu's "Open DevTools" registers as
        // a legacy target and Metro's /open-debugger can't find a fusebox target to open
        // (silent no-op). We use dangerouslyForceOverride (not override) because the flags
        // were already overridden by load(); a second override() would throw. This must run
        // before the inspector first reads the flag (i.e. before connecting to the packager),
        // which onCreate satisfies. We extend the New Architecture defaults so bridgeless /
        // Fabric / TurboModules stay enabled.
        ReactNativeFeatureFlags.dangerouslyForceOverride(
            object : ReactNativeNewArchitectureFeatureFlagsDefaults() {
                override fun fuseboxEnabledRelease(): Boolean = true
            }
        )
    }

    val bundleDownloadListenerHolder = BundleDownloadListenerHolder()

    override val devBundleDownloadListener: DevBundleDownloadListener
        get() = bundleDownloadListenerHolder

    override fun getUseDeveloperSupport(): Boolean {
        return SampleAppsManager.sampleAppJSBundlePath == null
    }

    override fun createSplashScreenPresenter(): MendixSplashScreenPresenter {
        return SplashScreenPresenter()
    }

    override val jsBundleProvider: JSBundleFileProvider?
        get() = JSBundleFileProvider { SampleAppsManager.sampleAppJSBundlePath }
}
