package com.mendix.developerapp.loading

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.MainApplication
import com.mendix.developerapp.R
import com.mendix.developerapp.ui.component.errorpage.ErrorPage
import java.net.URI

@Composable
fun MendixProjectLoaderScreen(viewModel: ProjectLoaderViewModel) {
    val state by viewModel.state.collectAsState()
    val animatedProgress = animateFloatAsState(
        targetValue = state.downloadProgress.toFloat() / 100,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value

    val uri = URI(state.appUrl)
    val errorMessageTitle: String
    var primaryButtonText = stringResource(R.string.button_troubleshooting)
    var secondaryButtonText = stringResource(R.string.button_retry)
    var primaryButtonOnPress = { viewModel.openInBrowser.invoke("https://docs.mendix.com/refguide/mobile/getting-started-with-mobile/prerequisites/") }
    var secondaryButtonOnPress = { viewModel.onRetryButtonPressed.invoke() }

    when (state.errorTypeId) {
        R.string.error_type_connection -> {
            errorMessageTitle = stringResource(R.string.error_connection_title)
        }

        R.string.error_type_version -> {
            errorMessageTitle = stringResource(R.string.error_version_title)
            primaryButtonText = stringResource(R.string.button_min_versions_guide)
            secondaryButtonText = stringResource(R.string.button_custom_developer_app_guide)
            primaryButtonOnPress = { viewModel.openInBrowser.invoke("https://docs.mendix.com/refguide/mobile/distributing-mobile-apps/building-native-apps/how-to-min-versions/") }
            secondaryButtonOnPress = { viewModel.openInBrowser.invoke("https://docs.mendix.com/howto8/mobile/how-to-devapps/") }
        }

        else -> {
            errorMessageTitle = stringResource(R.string.error_unknown_title)
            primaryButtonText = stringResource(R.string.button_troubleshooting)
            secondaryButtonText = stringResource(R.string.button_retry)
        }
    }

    val portNumber = if (uri.port == -1) { if (uri.scheme == "http") "80" else "443" } else uri.port
    val errorMessage = when (state.status) {
        ProjectLoaderViewModel.STATUS_ERROR_BUNDLE -> stringResource(R.string.error_js_bundle, uri, portNumber )
        ProjectLoaderViewModel.STATUS_ERROR_CONNECTION -> stringResource(R.string.error_runtime_connection, uri, portNumber)
        ProjectLoaderViewModel.STATUS_ERROR_NO_NATIVE_PROFIlE -> stringResource(R.string.error_missing_native_profile)
        ProjectLoaderViewModel.STATUS_ERROR_PACKAGER_CONNECTION -> stringResource(R.string.error_packager_not_running)
        ProjectLoaderViewModel.STATUS_ERROR_STUDIO_OUTDATED -> stringResource(R.string.error_min_outdated)
        ProjectLoaderViewModel.STATUS_ERROR_MIN_OUTDATED -> stringResource(R.string.error_min_outdated)
        else -> stringResource(R.string.error_unknown)
    }

    if(state.status == ProjectLoaderViewModel.STATUS_LOADING) {
        Image(
            painter = painterResource(id = MainApplication.backgroundImagePath),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_mendix_all_white),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp))
            Text(
                text = stringResource(id = state.loadingScreenText),
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp))
            LinearProgressIndicator(
                modifier = Modifier
                    .height(4.dp),
                color = Color.White,
                progress = animatedProgress
            )
        }
    } else {
        ErrorPage(
            title = errorMessageTitle,
            errorMessage = errorMessage,
            closePageButtonOnPress = {viewModel.onBack.invoke()},
            primaryButtonText = primaryButtonText,
            primaryButtonOnPress = primaryButtonOnPress,
            secondaryButtonText = secondaryButtonText,
            secondaryButtonOnPress = secondaryButtonOnPress
        )
    }
}

@Preview
@Composable
fun ProjectLoaderScreenPreview(){
    MendixProjectLoaderScreen(viewModel = ProjectLoaderViewModel())
}
