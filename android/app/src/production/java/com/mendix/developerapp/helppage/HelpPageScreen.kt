package com.mendix.developerapp.helppage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.component.CustomButton
import com.mendix.developerapp.ui.component.appbar.AppBarWithLogoXL
import com.mendix.developerapp.ui.theme.brandPrimary

@Composable
fun HelpPageScreen(viewModel: HelpPageViewModel){
    val buttonStyle: Modifier = Modifier.background(Color.Transparent).padding(0.dp, 0.dp, 0.dp, 8.dp)
    val buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
    )
    Column {
        AppBarWithLogoXL()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            CustomButton(
                modifier = buttonStyle,
                onClick = { viewModel.navigateToWelcomeScreen() },
                title = stringResource(R.string.help_how_to_use_make_it_native_app),
                buttonColors = buttonColors,
                border = BorderStroke(1.dp, brandPrimary),
                textColor = brandPrimary
            )
            CustomButton(
                modifier = buttonStyle,
                onClick = { viewModel.openWebPage("https://forum.mendix.com/") },
                title = stringResource(R.string.help_forum),
                buttonColors = buttonColors,
                border = BorderStroke(1.dp, brandPrimary),
                textColor = brandPrimary
            )
            CustomButton(
                modifier = buttonStyle,
                onClick = { viewModel.openWebPage("https://mendixcommunity.slack.com/") },
                title = stringResource(R.string.help_slack_community),
                buttonColors = buttonColors,
                border = BorderStroke(1.dp, brandPrimary),
                textColor = brandPrimary
            )
            CustomButton(
                modifier = buttonStyle,
                onClick = { viewModel.openWebPage("https://docs.mendix.com/refguide/troubleshooting/") },
                title = stringResource(R.string.help_troubleshooting),
                buttonColors = buttonColors,
                border = BorderStroke(1.dp, brandPrimary),
                textColor = brandPrimary
            )
            CustomButton(
                modifier = buttonStyle,
                onClick = { viewModel.openWebPage("https://docs.mendix.com/refguide/mobile/") },
                title = stringResource(R.string.help_mobile_doc),
                buttonColors = buttonColors,
                border = BorderStroke(1.dp, brandPrimary),
                textColor = brandPrimary
            )
        }
    }
}

@Preview
@Composable
fun HelpPageScreenPreview(){
    HelpPageScreen(viewModel = HelpPageViewModel())
}
