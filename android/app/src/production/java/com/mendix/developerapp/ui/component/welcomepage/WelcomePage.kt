package com.mendix.developerapp.ui.component.welcomepage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.MainApplication
import com.mendix.developerapp.ui.component.CustomCard
import com.mendix.developerapp.ui.theme.brandPrimary
import com.mendix.developerapp.util.OnBoardingPage
import com.mendix.developerapp.ui.theme.openSansFamily

@Composable
fun WelcomePage(
    onBoardingPage: OnBoardingPage,
    buttonNextVisible: Boolean = true,
    buttonNextText: String = "Continue",
    buttonNextOnClick: () -> Unit = {},
    buttonPreviousVisible: Boolean = true,
    buttonPreviousText: String = "Previous",
    buttonPreviousOnClick: () -> Unit = {},
) {
    Image(
        painter = painterResource(id = MainApplication.backgroundImagePath),
        contentDescription = null,
        contentScale = ContentScale.FillHeight
    )
    CustomCard(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(28.dp)) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.height(270.dp)
                ) {
                    // Icon
                    Image(
                        painter = painterResource(id = onBoardingPage.image),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(
                        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)
                    )
                    // Title
                    Text(
                        text = stringResource(id = onBoardingPage.title),
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = openSansFamily
                    )
                    Spacer(
                        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp)
                    )
                    // Description
                    Text(
                        text = stringResource(id = onBoardingPage.description),
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = openSansFamily
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    color = brandPrimary,
                    text = buttonPreviousText,
                    modifier = Modifier
                        .alpha(if (buttonPreviousVisible) 1f else 0f)
                        .clickable(enabled = true, onClick = buttonPreviousOnClick)
                )
                Text(
                    color = brandPrimary,
                    text = buttonNextText,
                    modifier = Modifier
                        .alpha(if (buttonNextVisible) 1f else 0f)
                        .clickable(enabled = true, onClick = buttonNextOnClick)
                )
            }
        }
    }
}

@Preview
@Composable
fun ContentWelcomePreview() {
    WelcomePage(OnBoardingPage.PreviewYourAppEffortlessly)
}
