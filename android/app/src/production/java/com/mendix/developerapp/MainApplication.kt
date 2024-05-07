package com.mendix.developerapp

import com.mendix.developerapp.sampelapps.SampleAppsManager
import com.mendix.developerapp.splashscreen.SplashScreenPresenter
import com.mendix.mendixnative.JSBundleFileProvider
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter

class MainApplication : BaseApplication() {
    override fun getUseDeveloperSupport(): Boolean {
        return SampleAppsManager.sampleAppJSBundlePath == null
    }

    override fun createSplashScreenPresenter(): MendixSplashScreenPresenter {
        return SplashScreenPresenter()
    }

    override val jsBundleProvider: JSBundleFileProvider?
        get() = JSBundleFileProvider { SampleAppsManager.sampleAppJSBundlePath }
}
