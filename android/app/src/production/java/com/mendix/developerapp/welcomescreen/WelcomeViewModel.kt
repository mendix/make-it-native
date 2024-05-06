package com.mendix.developerapp.welcomescreen

import androidx.lifecycle.ViewModel
import com.mendix.developerapp.util.OnBoardingPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class WelcomeState(
    val currentPageIndex: Int = 0,
    val pageCount: Int = 0
)

class WelcomeViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(WelcomeState())
    val state: StateFlow<WelcomeState> = mutableState.asStateFlow()

    var goToHomeScreen: (() -> Unit)? = null

    val onBoardingPages = listOf(
        OnBoardingPage.PreviewYourAppEffortlessly,
        OnBoardingPage.SeamlessNavigationAtYourFingertips,
        OnBoardingPage.TheHistory
    )

    init {
        mutableState.update { currentState ->
            currentState.copy(pageCount = onBoardingPages.size)
        }
    }

    fun nextPage(){
        if (onBoardingPages.size - 1 > mutableState.value.currentPageIndex) {
            mutableState.update { currentState ->
                currentState.copy(currentPageIndex = currentState.currentPageIndex + 1)
            }
        }else if (onBoardingPages.size - 1 == mutableState.value.currentPageIndex) {
            goToHomeScreen?.invoke()
        }
    }

    fun previousPage(){
        if (mutableState.value.currentPageIndex > 0) {
            mutableState.update { currentState ->
                currentState.copy(currentPageIndex = currentState.currentPageIndex - 1)
            }
        }
    }
}

