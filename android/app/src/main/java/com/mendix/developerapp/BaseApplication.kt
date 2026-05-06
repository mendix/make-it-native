package com.mendix.developerapp

import com.facebook.react.PackageList
import com.facebook.react.ReactPackage
import com.mendix.developerapp.utilities.CrashlyticsErrorHandler
import com.mendix.mendixnative.MendixReactApplication
import com.mendix.mendixnative.error.ErrorHandler

@Suppress("unused")
open class BaseApplication : MendixReactApplication() {

    override fun getPackages(): List<ReactPackage> {
        val packages: MutableList<ReactPackage> = PackageList(this).packages

        return packages
    }

    override fun getUseDeveloperSupport(): Boolean {
        return false
    }

    override fun createErrorHandler(): ErrorHandler {
        return CrashlyticsErrorHandler(this)
    }
}
