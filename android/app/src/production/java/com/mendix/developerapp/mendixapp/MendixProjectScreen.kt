package com.mendix.developerapp.mendixapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isNotEmpty
import com.mendix.developerapp.loading.ProjectLoaderViewModel

@Composable
fun MendixProjectScreen(viewModel: ProjectLoaderViewModel) {
    val state by viewModel.state.collectAsState()
    if (state.rnFrameLayout?.isNotEmpty() == true) {
        AndroidView(factory = {
            state.rnFrameLayout!!
        })
    }
}
