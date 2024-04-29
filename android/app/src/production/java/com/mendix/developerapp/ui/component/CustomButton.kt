package com.mendix.developerapp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    buttonColors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke = ButtonDefaults.outlinedBorder,
    enabled: Boolean = true,
    textColor: Color = Color.White,
    onClick: () -> Unit,
    title: String,
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(47.dp)
            .then(modifier),
        onClick = onClick,
        colors = buttonColors,
        border = border,
        enabled = enabled,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            disabledElevation = 0.dp
        )
    ) {
        Text(text = title, color = textColor)
    }
}

@Preview(showBackground = false)
@Composable
fun CustomButtonPreview(){
    CustomButton(
        onClick = {},
        title = "Button"
    )
}
