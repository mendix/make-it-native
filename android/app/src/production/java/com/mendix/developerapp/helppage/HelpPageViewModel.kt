package com.mendix.developerapp.helppage

import androidx.lifecycle.ViewModel

class HelpPageViewModel : ViewModel() {
    lateinit var openWebPage: (String) -> Unit
    lateinit var navigateToWelcomeScreen: () -> Unit
}