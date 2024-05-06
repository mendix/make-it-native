package com.mendix.developerapp.ui.component.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.R
import com.mendix.developerapp.history.History
import com.mendix.developerapp.ui.theme.grayPrimary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryListItem(
    history: History,
    historyItemOnClick: (History) -> Unit = {},
    favoriteButtonOnClick: (History) -> Unit = {},
) {
    val simpleDateFormat = SimpleDateFormat("EEE dd MMM yyyy hh:mm:ss", Locale("en", "US"))
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClick = { historyItemOnClick.invoke(history) }
            )) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = history.url,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
                Text(
                    text = simpleDateFormat.format(history.lastConnection).toString(),
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = grayPrimary
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clickable(
                        enabled = true,
                        onClick = {
                            history.let { favoriteButtonOnClick.invoke(it) }
                        }
                    )
            ){
                Image(
                    painter = painterResource(
                        id = if (history.isFavorite)
                            R.drawable.ic_star_selected
                        else
                            R.drawable.ic_star
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun HistoryListItemPreview() {
    HistoryListItem(
        History(
            id = 0,
            url = "http://10.0.2.2:8080",
            isFavorite = false,
            lastConnection = Date()
        )
    )
}
