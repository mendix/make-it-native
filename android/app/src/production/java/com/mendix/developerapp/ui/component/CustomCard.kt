package com.mendix.developerapp.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .then(modifier)
    ) {
        content()
    }
}

@Preview(showBackground = false)
@Composable
fun CustomCardPreview(){
    CustomCard(
        modifier = Modifier.fillMaxSize(),
        content = @Composable{
            Text(text = "Test")
        }
    )
}
