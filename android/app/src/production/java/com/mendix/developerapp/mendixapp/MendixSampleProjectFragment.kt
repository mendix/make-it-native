package com.mendix.developerapp.mendixapp

import android.os.Bundle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.mendix.developerapp.R
import com.mendix.developerapp.sampelapps.SampleAppsManager
import com.mendix.mendixnative.react.MxConfiguration
import com.mendix.mendixnative.react.clearData
import org.devio.rn.splashscreen.SplashScreen

class MendixSampleProjectFragment : MendixProjectFragmentBase() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SplashScreen.show(requireActivity())
        MxConfiguration.setDefaultAppNameOrDefault(SampleAppsManager.appName)
        MxConfiguration.setDefaultDatabaseNameOrDefault(SampleAppsManager.appName)
        MxConfiguration.setDefaultFilesDirectoryOrDefault(SampleAppsManager.appName)
        super.onCreate(savedInstanceState)
    }

    override fun onCloseProjectSelected() {
        super.onCloseProjectSelected()
        // We need to go back to gallery to reset the sample app status
        findNavController().navigate(R.id.nav_gallery, null, NavOptions.Builder().setPopUpTo(R.id.nav_mendix_sample_app, true).build())
    }

    override fun onDetach() {
        activity?.let { clearData(it.application) }
        SampleAppsManager.resetSelectedProject()
        super.onDetach()
    }
}
