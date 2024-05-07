package com.mendix.developerapp.utilities

import android.app.Activity
import com.mendix.developerapp.BuildConfig
import com.mendix.mendixnative.config.AppPreferences
import com.mendix.mendixnative.react.MxConfiguration.WarningsFilter

fun getWarningFilterValue(context: Activity): WarningsFilter {
    if (BuildConfig.DEBUG) {
        return WarningsFilter.all
    }
    return if (AppPreferences(context).isDevModeEnabled) WarningsFilter.partial else WarningsFilter.none
}
