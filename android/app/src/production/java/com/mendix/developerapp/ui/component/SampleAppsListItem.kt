package com.mendix.developerapp.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.mendix.developerapp.sampelapps.SampleAppViewInfo
import com.mendix.developerapp.ui.theme.grayPrimary
import java.io.File

@Composable
fun SampleAppsListItem(
    sampleAppViewInfo: SampleAppViewInfo,
    title: String,
    onClick: () -> Unit
) {
    val imgFile = File(sampleAppViewInfo.splashFilePath)
    var imgBitmap: Bitmap? = null
    if (imgFile.exists()) {
        imgBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    Column(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(10.dp))
                .fillMaxSize()
                .aspectRatio(1f)
                .background(grayPrimary)
        ){
            if (imgBitmap != null) {
                Image(
                    bitmap = imgBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillHeight
                )
            }
        }
        Spacer(modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
        )
        Text(
            text = title
        )
    }
}

class SampleAppViewInfoProvider: PreviewParameterProvider<SampleAppViewInfo> {
    override val values: Sequence<SampleAppViewInfo> = listOf(
        SampleAppViewInfo(
            id = "1",
            name = "Sample app 1",
            description = "Sample app description",
            splashFilePath = "atlas_ref_app"
        ),
    ).asSequence()
}

@Preview
@Composable
fun SampleAppsListItemPreview(
    @PreviewParameter(SampleAppViewInfoProvider::class) sampleAppViewInfoProvider: SampleAppViewInfo
) {
    SampleAppsListItem(
        sampleAppViewInfo = sampleAppViewInfoProvider,
        title = "Test",
        onClick = {}
    )
}