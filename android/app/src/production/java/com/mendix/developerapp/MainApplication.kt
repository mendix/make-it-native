package com.mendix.developerapp

import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
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
