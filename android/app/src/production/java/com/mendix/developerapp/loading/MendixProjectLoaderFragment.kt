package com.mendix.developerapp.loading

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.mendix.developerapp.R
import com.mendix.developerapp.home.HomeViewModel
import com.mendix.developerapp.ui.theme.MyApplicationTheme
import com.mendix.mendixnative.fragment.MendixReactFragment
import com.mendix.mendixnative.react.MendixApp
import java.io.Serializable

class MendixProjectLoaderFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private lateinit var runtimeUrl: String
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val viewModel: ProjectLoaderViewModel by viewModels()

    private lateinit var argComponentName: String
    private lateinit var argMendixApp: Serializable
    private var argLaunchOptions: Bundle? = null
    private var argClearData: Boolean = false
    private var argUseDeveloperSupport: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argComponentName = requireArguments().getString(MendixReactFragment.ARG_COMPONENT_NAME)!!
        argLaunchOptions = requireArguments().getBundle(MendixReactFragment.ARG_LAUNCH_OPTIONS)
        argMendixApp = requireArguments().getSerializable(MendixReactFragment.ARG_MENDIX_APP)!!
        argClearData = requireArguments().getBoolean(MendixReactFragment.ARG_CLEAR_DATA)
        argUseDeveloperSupport = requireArguments().getBoolean(MendixReactFragment.ARG_USE_DEVELOPER_SUPPORT)

        runtimeUrl = (requireArguments().getSerializable(MendixReactFragment.ARG_MENDIX_APP) as MendixApp).runtimeUrl
        viewModel.runPreChecks(checkAppUrl = runtimeUrl)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyListeners()
        initialize()

        composeView.setContent {
            MyApplicationTheme {
                MendixProjectLoaderScreen(viewModel)
            }
        }
    }

    private fun buttonGoBackOnPressListener() {
        findNavController().popBackStack()
    }

    private fun openUrlInBrowserListener(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun buttonRetryConnectionOnPressListener() {
        retry()
    }

    private fun applyListeners() {
        viewModel.onBack = this::buttonGoBackOnPressListener
        viewModel.openInBrowser = this::openUrlInBrowserListener
        viewModel.onRetryButtonPressed = this::buttonRetryConnectionOnPressListener
    }

    private fun initialize() {
        viewModel.setAppUrl(runtimeUrl)
        viewModel.preChecksPassed.observe(viewLifecycleOwner) {
            if (it) {
                homeViewModel.saveHistory(runtimeUrl)
                navigateToProject()
            }
        }
    }

    private fun retry() {
        findNavController().navigate(R.id.nav_loader, Bundle().also {
            it.putString(MendixReactFragment.ARG_COMPONENT_NAME, argComponentName)
            it.putBundle(MendixReactFragment.ARG_LAUNCH_OPTIONS, argLaunchOptions)
            it.putSerializable(MendixReactFragment.ARG_MENDIX_APP, argMendixApp)
            it.putBoolean(MendixReactFragment.ARG_CLEAR_DATA, argClearData)
            it.putBoolean(MendixReactFragment.ARG_USE_DEVELOPER_SUPPORT, argUseDeveloperSupport)
        }, NavOptions.Builder().setPopUpTo(R.id.nav_loader, true).build())
    }

    private fun navigateToProject() {
        requireActivity().runOnUiThread {
            findNavController().navigate(
                MendixProjectLoaderFragmentDirections.actionMendixProjectLoaderFragmentToMendixProjectFragment(
                    argComponentName, argMendixApp, argLaunchOptions, argClearData, argUseDeveloperSupport
                )
            )
        }
    }
}
