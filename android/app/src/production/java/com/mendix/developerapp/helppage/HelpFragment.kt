package com.mendix.developerapp.helppage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.theme.MyApplicationTheme

class HelpFragment : BaseFragment() {

    private lateinit var composeView: ComposeView
    private val viewModel: HelpPageViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.openWebPage = this::openWebpage
        viewModel.navigateToWelcomeScreen = this::navigateToWelcomeScreen
        composeView.setContent {
            MyApplicationTheme {
                HelpPageScreen(viewModel)
            }
        }
    }

    private fun openWebpage(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun navigateToWelcomeScreen() {
        findNavController().navigate(
            R.id.nav_welcome_screen,
            null,
            NavOptions.Builder().setPopUpTo(R.id.nav_help, true).build()
        )
    }
}
