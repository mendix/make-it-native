package com.mendix.developerapp

import android.content.Context
import com.facebook.react.PackageList
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactPackage
import com.mendix.mendixnative.MendixReactApplication
import com.microsoft.codepush.react.CodePush
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

        // Packages that cannot be autolinked yet can be added manually here:
        packages.add(CodePush(codePushKey, applicationContext, BuildConfig.DEBUG))
        return packages
    }

    override fun getUseDeveloperSupport(): Boolean {
        return false
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
