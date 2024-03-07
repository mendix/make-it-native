package com.mendix.developerapp.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.ui.theme.brandPrimary
import com.mendix.developerapp.ui.theme.grayPrimary
import com.mendix.developerapp.ui.theme.graySuperLight

@Composable
fun CustomSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)?,
    title: String? = ""
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val alignment by animateAlignmentAsState(if (checked) 1f else -1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        if (!title.isNullOrEmpty()) {
            Text(text = title)
        }

        Box(
            modifier = Modifier
                .size(width = 31.dp, height = 20.dp)
                .border(
                    width = 1.dp,
                    color = if (checked) brandPrimary else grayPrimary,
                    shape = RoundedCornerShape(percent = 50)
                )
                .background(
                    color = if(checked) brandPrimary else Color.White,
                    shape = CircleShape
                )
                .clickable(
                    indication = null,
                    interactionSource = interactionSource
                ) {
                    onCheckedChange?.invoke(!checked)
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = 2.dp,
                        end = 2.dp
                    )
                    .fillMaxSize(),
                contentAlignment = alignment
            ) {
                Box(
                    modifier = Modifier
                        .size(size = 17.dp)
                        .border(
                            1.dp,
                            if (checked) brandPrimary else graySuperLight,
                            CircleShape
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = CircleShape,
                            ambientColor = grayPrimary
                        )
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        ),
                )
            }
        }
    }
}

@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue)
    return derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) }
}

@Preview(showBackground = false)
@Composable
fun CustomSwitchPreview(){
    CustomSwitch(
        onCheckedChange = {}
    )
}
