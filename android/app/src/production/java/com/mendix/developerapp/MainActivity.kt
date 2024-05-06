package com.mendix.developerapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import com.mendix.developerapp.databinding.ActivityMainBinding
import com.mendix.developerapp.home.HomeViewModel
import com.mendix.developerapp.mendixapp.MendixProjectFragment
import com.mendix.developerapp.utilities.GlobalTouchEventListener
import com.mendix.developerapp.utilities.getWarningFilterValue
import com.mendix.developerapp.utilities.supportsAR
import com.mendix.mendixnative.fragment.BackButtonHandler
import com.mendix.mendixnative.MendixApplication
import com.mendix.mendixnative.fragment.MendixReactFragment
import com.mendix.mendixnative.activity.LaunchScreenHandler
import com.mendix.mendixnative.config.AppPreferences
import com.mendix.mendixnative.react.MendixApp
import com.mendix.mendixnative.react.splash.MendixSplashScreenPresenter

const val PREF_FIRST_TIME = "pref_first_time"
const val PREF_AR_ERROR_SHOWN = "pref_ar_error_shown"

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler, LaunchScreenHandler,
    PermissionAwareActivity {
    private lateinit var binding: ActivityMainBinding
    private val currentFragment: Fragment?
        get() {
            return navHostFragment.childFragmentManager.fragments.first()
        }

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var preferences: AppPreferences
    private lateinit var devAppPreferences: SharedPreferences

    private var splashScreenPresenter: MendixSplashScreenPresenter? =
        (application as? MendixApplication)?.createSplashScreenPresenter()

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_loader || destination.id == R.id.nav_mendix_app || destination.id == R.id.nav_welcome_screen || destination.id == R.id.nav_mendix_sample_app) {
                actionBar?.hide()
                binding.bottomNav.visibility = View.GONE
            } else {
                actionBar?.show()
                binding.bottomNav.visibility = View.VISIBLE
            }

            if (destination.id == R.id.nav_mendix_app || destination.id == R.id.nav_mendix_sample_app) {
                // Checks the current theme and apply the correct style (Backwards compatible)
                val isDarkMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    resources.configuration.isNightModeActive;
                } else {
                    AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES && Configuration.UI_MODE_NIGHT_MASK ==
                            Configuration.UI_MODE_NIGHT_YES
                }
                theme.applyStyle(if (isDarkMode) R.style.Theme_Mendix_Dark else R.style.Theme_Mendix, true)
            } else {
                theme.applyStyle(R.style.Theme_MakeItNative, true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = AppPreferences(applicationContext)
        devAppPreferences = getPreferences(MODE_PRIVATE)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        setupHomeViewModel()
        setupNavigation()
        setupActionBar()
    }

    override fun onResume() {
        super.onResume()
        navHostFragment.navController.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onPause() {
        navHostFragment.navController.removeOnDestinationChangedListener(
            onDestinationChangedListener
        )
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        (currentFragment as? MendixReactFragment)?.onNewIntent(intent)
    }

    private fun setupHomeViewModel() {
        homeViewModel.setup(
            preferences.appUrl,
            true,
            preferences.isDevModeEnabled,
            supportsAR(this),
            devAppPreferences.getBoolean(PREF_AR_ERROR_SHOWN, false)
        )
    }

    private fun setupActionBar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNav, navHostFragment.navController)

        if (devAppPreferences.getBoolean(PREF_FIRST_TIME, true)) {
            devAppPreferences.edit().let {
                it.putBoolean(PREF_FIRST_TIME, false)
                it.commit()
            }
            navHostFragment.navController.navigate(
                R.id.nav_welcome_screen,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_home, true).build()
            )
        }
    }

    private fun handleLaunchedWithExtras(intent: Intent): Boolean {
        val isDeepLink = intent.action != null && intent.data?.scheme == "makeitnative"
        if (isDeepLink) {
            startMendixApp(false)
            return true
        }
        return false
    }

    private fun startMendixApp(clearData: Boolean) {
        navHostFragment.navController.navigate(R.id.nav_mendix_app, Bundle().also {
            it.putBoolean(MendixProjectFragment.ARG_IS_SAMPLE_APP, false)
            it.putString(
                MendixReactFragment.ARG_COMPONENT_NAME,
                getString(R.string.react_native_component_name)
            )
            it.putSerializable(
                MendixReactFragment.ARG_MENDIX_APP, MendixApp(
                    homeViewModel.state.value.appUrl,
                    getWarningFilterValue(this),
                    homeViewModel.state.value.devModeEnabled,
                    true
                )
            )
            it.putBoolean(MendixReactFragment.ARG_CLEAR_DATA, clearData)
            it.putBoolean(MendixReactFragment.ARG_USE_DEVELOPER_SUPPORT, true)
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return if ((currentFragment as? GlobalTouchEventListener)?.dispatchTouchEvent(ev) == true) {
            true
        } else super.dispatchTouchEvent(ev)
    }

    override fun onBackPressed() {
        if ((currentFragment as? BackButtonHandler)?.onBackPressed() != true) {
            super.onBackPressed()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        /**
         * React Native expects to handle config changes internally.
         * That basically means none of the android niceties for orientation changes work out of the box
         * By navigating again to the Fragment we are ensuring it will get the correct layout for the orientation manually.
         */
        navHostFragment.navController.currentDestination?.label?.let {
            // Using labels here as it's not possible afaik to get the action id for some actions
            if (it != "fragment_mendix_project" && it != "fragment_mendix_sample_app") {
                val id = navHostFragment.navController.currentDestination!!.id
                navHostFragment.navController.navigate(
                    id,
                    currentFragment?.arguments,
                    NavOptions.Builder().setPopUpTo(id, true).build()
                )
            }
        }
    }

    override fun showLaunchScreen() {
        splashScreenPresenter?.show(this)
    }

    override fun hideLaunchScreen() {
        splashScreenPresenter?.hide(this)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return if ((currentFragment as? MendixReactFragment)?.onKeyUp(
                keyCode,
                event
            ) == true
        ) true else super.onKeyUp(keyCode, event)
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }

    override fun requestPermissions(
        permissions: Array<out String>,
        requestCode: Int,
        listener: PermissionListener?
    ) {
        (currentFragment as? MendixReactFragment)?.requestPermissions(
            permissions,
            requestCode,
            listener
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (currentFragment as? MendixReactFragment)?.onActivityResult(requestCode, resultCode, data)
            ?: super.onActivityResult(requestCode, resultCode, data)
    }
}

