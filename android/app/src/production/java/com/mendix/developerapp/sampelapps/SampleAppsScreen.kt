package com.mendix.developerapp.sampelapps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.ui.component.SampleAppsListItem
import com.mendix.developerapp.ui.component.appbar.AppBarWithLogo

@Composable
fun SampleAppsScreen(viewModel: SampleAppsViewModel) {
    val itemsList = viewModel.sampleAppsManager.apps

    Column {
        AppBarWithLogo()

        Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 70.dp)
            ){
                itemsIndexed(itemsList) { index, item ->
                    SampleAppsListItem(
                        sampleAppViewInfo = viewModel.sampleAppsManager.getSampleAppInfo(index),
                        title = item.name,
                        onClick = {
                            viewModel.openSampleApp(item)
                        }
                    )
                }
            }
        }
    }
}
