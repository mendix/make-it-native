package com.mendix.developerapp.history

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import com.mendix.developerapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.ui.component.appbar.AppBarWithLogo
import com.mendix.developerapp.ui.component.history.HistoryListItem
import com.mendix.developerapp.ui.component.history.HistorySwipeBackground
import com.mendix.developerapp.ui.theme.grayPrimary

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val state by viewModel.state.collectAsState()

    Column {
        AppBarWithLogo()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (state.historyList.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                    ) {
                        items(
                            items = state.historyList,
                            key = { it.url },
                            itemContent = {item ->
                                val currentItem by rememberUpdatedState(item)
                                val dismissState = rememberDismissState(
                                    confirmStateChange = {
                                        when (it) {
                                            DismissValue.DismissedToStart -> {
                                                currentItem.let { history -> viewModel.removeHistory(history) }
                                                true
                                            }
                                            else -> {false}
                                        }
                                    }
                                )
                                SwipeToDismiss(
                                    state = dismissState,
                                    modifier = Modifier
                                        .padding(vertical = 1.dp)
                                        .animateItemPlacement(),
                                    background = {
                                        HistorySwipeBackground(dismissState = dismissState)
                                    },
                                    directions = setOf(DismissDirection.EndToStart),
                                    dismissContent = {
                                        Column {
                                            HistoryListItem(
                                                history = item,
                                                favoriteButtonOnClick = viewModel::favoriteButtonOnClick,
                                                historyItemOnClick = viewModel::historyListItemOnClick
                                            )
                                            if (state.historyList.size > 0 && item != state.historyList[state.historyList.size - 1]) {
                                                Divider(
                                                    color = grayPrimary,
                                                    thickness = 1.dp
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    }
                } else {
                    Text(
                        text = stringResource(id = R.string.history_no_apps_launched),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HistoryScreenPreview(){
    HistoryScreen(viewModel = HistoryViewModel(Application()))
}
