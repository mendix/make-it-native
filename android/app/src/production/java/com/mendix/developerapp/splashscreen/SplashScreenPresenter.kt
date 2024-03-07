package com.mendix.developerapp.splashscreen

import android.app.Activity
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter
import org.devio.rn.splashscreen.SplashScreen

class SplashScreenPresenter : MendixSplashScreenPresenter {
    override fun show(activity: Activity) {
        hide(activity)
        SplashScreen.show(activity, true)
    }

    override fun hide(activity: Activity) = SplashScreen.hide(activity)
}
