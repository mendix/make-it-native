package com.mendix.developerapp.splashscreen

import android.app.Activity
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter

class SplashScreenPresenter : MendixSplashScreenPresenter {
    override fun show(activity: Activity) {
        // react-native-bootsplash handles the native splash screen automatically
        // No need to manually show it here
    }

    override fun hide(activity: Activity) {
        // react-native-bootsplash will be hidden from JavaScript using RNBootSplash.hide()
        // No need to manually hide it here
    }
}
