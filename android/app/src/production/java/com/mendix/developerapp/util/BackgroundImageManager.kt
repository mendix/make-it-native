package com.mendix.developerapp.util

import com.mendix.developerapp.R

fun pickRandomBackground(): Int {
    return (0..11).random()
}

fun imageBackground(index: Int = 0): Int {
    return listOf(
        R.drawable.background_a,
        R.drawable.background_b,
        R.drawable.background_c,
        R.drawable.background_d,
        R.drawable.background_e,
        R.drawable.background_f,
        R.drawable.background_g,
        R.drawable.background_h,
        R.drawable.background_i,
        R.drawable.background_j,
        R.drawable.background_k,
        R.drawable.background_l,
    )[index]
}

fun splashBackground(index: Int = 0): Int {
    return listOf(
        R.drawable.splash_a,
        R.drawable.splash_b,
        R.drawable.splash_c,
        R.drawable.splash_d,
        R.drawable.splash_e,
        R.drawable.splash_f,
        R.drawable.splash_g,
        R.drawable.splash_h,
        R.drawable.splash_i,
        R.drawable.splash_j,
        R.drawable.splash_k,
        R.drawable.splash_l,
    )[index]
}
