package com.mendix.developerapp.welcomescreen

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

class WelcomeFragment : BaseFragment() {
    private lateinit var composeView: ComposeView
    private val viewModel: WelcomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        composeView.setContent {
            MyApplicationTheme {
                WelcomeScreen(viewModel)
            }
        }
    }

    private fun setup() {
        viewModel.goToHomeScreen = this::navigateToHome
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.nav_home,
            null,
            NavOptions.Builder().setPopUpTo(R.id.nav_welcome_screen, true).build()
        )
    }
}
