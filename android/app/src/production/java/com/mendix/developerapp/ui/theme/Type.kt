package com.mendix.developerapp.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.R

val openSansFamily = FontFamily(
        Font(R.font.open_sans_light, FontWeight.Light),
        Font(R.font.open_sans_regular, FontWeight.Normal),
        Font(R.font.open_sans_semi_bold, FontWeight.SemiBold)
)

val Typography = Typography(
        body1 = TextStyle(
                fontFamily = openSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
        ),
        button = TextStyle(
                fontFamily = openSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
        ),
        caption = TextStyle(
                fontFamily = openSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
        )
)