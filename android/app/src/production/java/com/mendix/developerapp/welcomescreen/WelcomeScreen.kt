package com.mendix.developerapp.welcomescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.component.welcomepage.WelcomePage

@Composable
fun WelcomeScreen(viewModel: WelcomeViewModel) {
    val state by viewModel.state.collectAsState()

    WelcomePage(
        onBoardingPage = viewModel.onBoardingPages[state.currentPageIndex],
        buttonNextOnClick = viewModel::nextPage,
        buttonPreviousOnClick = viewModel::previousPage,
        buttonNextVisible = state.currentPageIndex < state.pageCount,
        buttonPreviousVisible = state.currentPageIndex > 0,
        buttonNextText =
            if (state.currentPageIndex == viewModel.onBoardingPages.size - 1)
                stringResource(id = R.string.btn_start)
            else
                stringResource(id = R.string.btn_continue)
    )
}