package com.mendix.developerapp

import android.app.Activity
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter
import com.mendix.mendixnative.util.ResourceReader.*

class MainApplication: BaseApplication() {
    override fun getUseDeveloperSupport(): Boolean {
        return readBoolean(this, "is_devapp")
    }

    override fun createSplashScreenPresenter(): MendixSplashScreenPresenter {
        return object : MendixSplashScreenPresenter {
            override fun show(activity: Activity) {
                // nothing
            }

            override fun hide(activity: Activity) {
                // nothing
            }
        }
    }
}
