package com.mendix.developerapp.mendixapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.mendix.developerapp.MendixBaseFragment
import com.mendix.developerapp.loading.ProjectLoaderViewModel
import com.mendix.developerapp.ui.theme.MyApplicationTheme
import com.mendix.developerapp.utilities.GlobalTouchEventListener
import org.devio.rn.splashscreen.SplashScreen

open class MendixProjectFragmentBase : MendixBaseFragment(), GlobalTouchEventListener {
    private lateinit var composeView: ComposeView
    protected val viewModel: ProjectLoaderViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            MyApplicationTheme {
                MendixProjectScreen(viewModel)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        viewModel.setRNFrameLayout(frameLayout)

        return ComposeView(requireContext()).also {
            composeView = it
        }
    }
}