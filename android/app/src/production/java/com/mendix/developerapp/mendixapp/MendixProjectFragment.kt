package com.mendix.developerapp.mendixapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.facebook.react.devsupport.overrideDevLoadingViewController
import com.facebook.react.devsupport.setBundleDownloadListener
import com.mendix.developerapp.MendixBaseFragment
import com.mendix.developerapp.databinding.ContentMendixProjectLoaderBinding
import com.mendix.developerapp.utilities.EmptyDevLoadingViewController
import com.mendix.developerapp.utilities.GlobalTouchEventListener
import com.mendix.developerapp.viewmodels.ProjectLoaderViewModel
import com.mendix.mendixnative.react.MxConfiguration
import org.devio.rn.splashscreen.SplashScreen

class MendixProjectFragment : MendixBaseFragment(), GlobalTouchEventListener {
    val viewModel: ProjectLoaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        MxConfiguration.setDefaultAppNameOrDefault(null)
        MxConfiguration.setDefaultDatabaseNameOrDefault(null)
        MxConfiguration.setDefaultFilesDirectoryOrDefault(null)

        super.onCreate(savedInstanceState)

        // Setting up our download listener
        reactNativeHost.reactInstanceManager.devSupportManager.apply {
            setBundleDownloadListener(this, viewModel.devServerCallback)
            overrideDevLoadingViewController(this, EmptyDevLoadingViewController())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.status.observe(viewLifecycleOwner) {
            if (it === ProjectLoaderViewModel.STATUS_SUCCESS) {
                SplashScreen.show(requireActivity())
            } else {
                SplashScreen.hide(requireActivity())
            }
        }

        /**
         * Why aren't we just attaching to ReactRootView directly? ReactRootView is fairly special.
         * Layouting is actually handled by the UIManagerModule, which means whatever child is added out of React Native's context
         * to the ReactRootView is basically ignored and gets no layout call nor parameters.
         */
        val frameLayout = FrameLayout(requireContext())
        frameLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        frameLayout.addView(view)

        val binding =
            ContentMendixProjectLoaderBinding.inflate(inflater, frameLayout as ViewGroup, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        frameLayout.addView(binding.root)

        return frameLayout
    }

    override fun onCloseProjectSelected() {
        super.onCloseProjectSelected()
        findNavController().navigate(MendixProjectFragmentDirections.actionMendixProjectFragmentToStartAppFragment())
    }

    companion object {
        const val ARG_IS_SAMPLE_APP = "arg_is_sample_app"
    }
}
