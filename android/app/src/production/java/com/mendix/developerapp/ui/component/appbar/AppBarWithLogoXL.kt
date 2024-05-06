package com.mendix.developerapp.ui.component.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.MainApplication
import com.mendix.developerapp.R

@Composable
fun AppBarWithLogoXL(){
    Box(
        modifier = Modifier
            .height(216.dp)
            .fillMaxWidth()
            .paint(
                painterResource(id = MainApplication.splashImagePath),
                contentScale = ContentScale.Crop
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_mendix_all_white),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .size(32.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppBarWithLogoXLPreview(){
    AppBarWithLogoXL()
}
