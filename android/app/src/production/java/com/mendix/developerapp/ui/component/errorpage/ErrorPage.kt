package com.mendix.developerapp.ui.component.errorpage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.component.CustomButton
import com.mendix.developerapp.ui.theme.brandPrimary
import com.mendix.developerapp.ui.theme.grayLight

@Composable
fun ErrorPage(
    closePageButtonOnPress: () -> Unit,
    primaryButtonOnPress: () -> Unit,
    primaryButtonText: String = stringResource(id = R.string.button_troubleshooting),
    primaryButtonEnabled: Boolean = true,
    secondaryButtonOnPress: () -> Unit,
    secondaryButtonText: String = stringResource(id = R.string.button_retry),
    secondaryButtonEnabled: Boolean = true,
    title: String = stringResource(id = R.string.error_unknown_title),
    errorMessage: String = stringResource(id = R.string.error_unknown)
) {
    val buttonStyle: Modifier = Modifier
        .background(Color.Transparent)
        .padding(0.dp, 0.dp, 0.dp, 8.dp)
    val buttonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(grayLight)
            .padding(top = 8.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 10.dp,
                        topEnd = 10.dp
                    )
                )
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { closePageButtonOnPress.invoke() }) {
                    Image(
                        modifier = Modifier.size(30.dp),
                        painter = painterResource(R.drawable.close_button),
                        contentDescription = null,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.alert),
                    contentDescription = null,
                    Modifier.size(32.dp)
                )
            }

            Text(
                text = title,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = errorMessage,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    lineHeight = 24.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (primaryButtonEnabled) {
                    CustomButton(
                        modifier = buttonStyle,
                        onClick = { primaryButtonOnPress.invoke() },
                        title = primaryButtonText,
                        buttonColors = buttonColors,
                        border = BorderStroke(1.dp, brandPrimary),
                        textColor = brandPrimary
                    )
                }
                if (secondaryButtonEnabled) {
                    CustomButton(
                        modifier = buttonStyle,
                        onClick = { secondaryButtonOnPress.invoke() },
                        title = secondaryButtonText,
                        buttonColors = buttonColors,
                        border = BorderStroke(1.dp, brandPrimary),
                        textColor = brandPrimary
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview(){
    ErrorPage(
        closePageButtonOnPress = {},
        primaryButtonOnPress = {},
        secondaryButtonOnPress = {}
    )
}
