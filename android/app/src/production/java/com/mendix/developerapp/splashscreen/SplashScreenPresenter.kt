package com.mendix.developerapp.splashscreen

import android.app.Activity
import com.zoontek.rnbootsplash.RNBootSplash
import com.mendix.developerapp.R
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter

class SplashScreenPresenter : MendixSplashScreenPresenter {
    override fun show(activity: Activity) {
        RNBootSplash.init(activity, R.style.BootTheme)
    }

    override fun hide(activity: Activity) {
    }
}