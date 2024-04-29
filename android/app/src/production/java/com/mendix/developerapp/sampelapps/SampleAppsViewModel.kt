package com.mendix.developerapp.sampelapps

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.mendix.mendixnative.react.MendixApp
import com.mendix.mendixnative.react.MxConfiguration

class SampleAppsViewModel(val sampleAppsManager: SampleAppsManager, private val navController: NavController) : ViewModel() {
    fun openSampleApp(sampleApp: SampleApp){
        sampleAppsManager.selectProject(sampleApp.id)
        navController.run{
            navigate(
                SampleAppsFragmentDirections.actionNavGalleryToNavMendixSampleApp(
                    argComponentName = "App",
                    argLaunchOptions = null,
                    argMendixApp = MendixApp(sampleApp.runtimeUrl, MxConfiguration.WarningsFilter.none, showExtendedDevMenu = false, attachCustomDeveloperMenu = true),
                    argClearData = true,
                    argUseDeveloperSupport = false,
                )
            )
        }
    }
}
