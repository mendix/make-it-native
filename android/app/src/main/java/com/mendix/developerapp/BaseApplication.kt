package com.mendix.developerapp

import android.content.Context
import com.facebook.react.PackageList
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactPackage
import com.mendix.developerapp.utilities.CrashlyticsErrorHandler
import com.mendix.mendixnative.MendixReactApplication
import com.mendix.mendixnative.error.ErrorHandler
import java.lang.reflect.InvocationTargetException

@Suppress("unused")
open class BaseApplication : MendixReactApplication() {

    override fun onCreate() {
        super.onCreate()
        if(reactNativeHost.hasInstance()) {
            initializeFlipper(this, reactNativeHost.reactInstanceManager)
        }
    }

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

    private fun initializeFlipper(context: Context, reactInstanceManager: ReactInstanceManager) {
        if (BuildConfig.DEBUG) {
            try {
                /*
             * We use reflection here to pick up the class that initializes Flipper, since
             * Flipper library is not available in release mode
             */
                val aClass = Class.forName("com.mendix.developerapp.flipper.ReactNativeFlipper")
                aClass.getMethod(
                    "initializeFlipper",
                    Context::class.java,
                    ReactInstanceManager::class.java
                ).invoke(
                    null, context,
                    reactInstanceManager
                )
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }
    }
}
