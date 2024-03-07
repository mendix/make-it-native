package com.mendix.developerapp.sampelapps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mendix.developerapp.BaseFragment
import com.mendix.developerapp.ui.theme.MyApplicationTheme

class SampleAppsFragment: BaseFragment() {
    private val viewModel: SampleAppsViewModel by viewModels {
        return@viewModels object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SampleAppsViewModel(SampleAppsManager(requireContext(), requireActivity().filesDir.absolutePath), findNavController()) as (T)
            }
        }
    }

    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
                SampleAppsScreen(viewModel)
            }
        }
    }
}