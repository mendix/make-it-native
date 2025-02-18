package com.mendix.developerapp.mendixapp

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.facebook.react.devsupport.DefaultDevLoadingViewImplementation
import com.facebook.react.devsupport.setBundleDownloadListener
import com.mendix.mendixnative.react.MxConfiguration

class MendixProjectFragment : MendixProjectFragmentBase() {

    companion object {
        const val ARG_IS_SAMPLE_APP = "arg_is_sample_app"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MxConfiguration.setDefaultAppNameOrDefault(null)
        MxConfiguration.setDefaultDatabaseNameOrDefault(null)
        MxConfiguration.setDefaultFilesDirectoryOrDefault(null)

        super.onCreate(savedInstanceState)

        // Setting up our download listener
        reactNativeHost.reactInstanceManager.devSupportManager.apply {
            setBundleDownloadListener(this, viewModel.devServerCallback)
        }

        // This now uses built-in RN mechanism to show bundling progress.
        DefaultDevLoadingViewImplementation.setDevLoadingEnabled(true);
    }

    override fun onCloseProjectSelected() {
        super.onCloseProjectSelected()
        findNavController().navigate(MendixProjectFragmentDirections.actionMendixProjectFragmentToStartAppFragment())
    }
}
