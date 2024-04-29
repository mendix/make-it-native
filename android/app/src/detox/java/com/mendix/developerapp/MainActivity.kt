package com.mendix.developerapp

import android.os.Bundle
import com.mendix.mendixnative.activity.MendixReactActivity
import com.mendix.mendixnative.config.AppUrl
import com.mendix.mendixnative.react.MendixApp
import com.mendix.mendixnative.react.MxConfiguration

class MainActivity : MendixReactActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        mendixApp = MendixApp(
            runtimeUrl = AppUrl.getUrlFromResource(this),
            warningsFilter = MxConfiguration.WarningsFilter.none,
            showExtendedDevMenu = false,
            attachCustomDeveloperMenu = false
        )
        super.onCreate(savedInstanceState)
    }
}
