package com.mendix.developerapp.util

import androidx.annotation.DrawableRes
import com.mendix.developerapp.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: Int,
    val description: Int
) {
    object PreviewYourAppEffortlessly : OnBoardingPage(
        image = R.drawable.welcome_qrcode,
        title = R.string.welcome_preview_app_effortlessly_title,
        description = R.string.welcome_preview_app_effortlessly_text
    )
    object TheHistory : OnBoardingPage(
        image = R.drawable.welcome_history,
        title = R.string.welcome_the_history_title,
        description = R.string.welcome_the_history_text
    )
    object SeamlessNavigationAtYourFingertips : OnBoardingPage(
        image = R.drawable.welcome_dev_gesture,
        title = R.string.welcome_seamless_navigation_at_your_fingertips_title,
        description = R.string.welcome_seamless_navigation_at_your_fingertips_text
    )
}
