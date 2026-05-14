package com.mendix.developerapp.mendixapp

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import com.mendix.developerapp.MainApplication
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

        // Wire the ViewModel's download listener into the application-level holder.
        // The holder was injected into the DevSupportManager at ReactHost creation time.
        (requireActivity().application as MainApplication)
            .bundleDownloadListenerHolder.delegate = viewModel.devServerCallback
    }

    override fun onDestroy() {
        (requireActivity().application as? MainApplication)
            ?.bundleDownloadListenerHolder?.delegate = null
        super.onDestroy()
    }

    override fun onCloseProjectSelected() {
        super.onCloseProjectSelected()
        findNavController().navigate(MendixProjectFragmentDirections.actionMendixProjectFragmentToStartAppFragment())
    }
}
