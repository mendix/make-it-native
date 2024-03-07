package com.mendix.developerapp.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.ui.theme.*

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
){
    val focusManager = LocalFocusManager.current
    val defaultBorderColor = grayPrimary
    val focusedBorderColor = brandPrimary
    var borderColor by remember { mutableStateOf(defaultBorderColor) }

    Row(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .height(40.dp)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        BasicTextField(
            modifier = Modifier
                .onFocusChanged {
                    borderColor = if (it.isFocused) {
                        focusedBorderColor
                    } else {
                        defaultBorderColor
                    }
                }
                .padding(8.dp, 0.dp, 0.dp, 0.dp),
            value = value,
            singleLine = true,
            maxLines = 1,
            onValueChange = onValueChange,
            keyboardActions = KeyboardActions(
                onAny = {
                    focusManager.clearFocus()
                }
            ),
            decorationBox = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = grayLight,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )
                    }
                    it()
                }
            },
            keyboardOptions = keyboardOptions
        )
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview(){
    CustomTextField(
        value = "",
        placeholder = "Place holder",
        onValueChange = {}
    )
}
