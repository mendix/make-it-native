package com.mendix.developerapp.home

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import com.mendix.developerapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mendix.developerapp.ui.component.CustomButton
import com.mendix.developerapp.ui.component.CustomSwitch
import com.mendix.developerapp.ui.component.CustomTextField
import com.mendix.developerapp.ui.component.PreviewViewComposable
import com.mendix.developerapp.ui.component.appbar.AppBarWithLogoXL
import com.mendix.developerapp.ui.theme.*

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.state.collectAsState()
    val localFocusManager = LocalFocusManager.current

    if(state.showQRCodeDialog){
        BackHandler {
            viewModel.toggleQRCodeDialog()
        }
        Box(modifier = Modifier.fillMaxSize()){
            PreviewViewComposable(onSuccess = viewModel::onBarCodeScanSuccess)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding()
            ) {
                IconButton(
                    onClick = { viewModel.qrCodeScannerButtonOnClick() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                        )
                        Text(
                            text = stringResource(id = R.string.qr_cancel_button),
                            fontSize = 14.sp, 
                            color = Color.White, 
                            fontWeight = FontWeight.W600
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .padding(vertical = 19.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .background(
                                color = grayPrimary.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        text = stringResource(id = R.string.scan_qr_code_caption),
                        fontSize = 14.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }
    } else {
    Column(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = {
                localFocusManager.clearFocus()
            })
        }
    ){
            AppBarWithLogoXL()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.weight(1f),
                        value = state.appUrl,
                        onValueChange = { viewModel.setAppUrl(it) },
                        placeholder = "http://runtime-url:8080",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Uri
                        )
                    )
                    Spacer(modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, brandPrimary, RoundedCornerShape(4.dp))
                            .clickable(
                                enabled = true,
                                onClick = { viewModel.qrCodeScannerButtonOnClick() }
                            )
                            .padding(2.dp)
                            .width(40.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.scan_qr),
                            contentDescription = null
                        )
                    }
                }

                Divider(
                    color = grayPrimary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(0.dp, 24.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CustomSwitch(
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
                        title = "Clear cache",
                        checked = state.clearData,
                        onCheckedChange = { viewModel.setClearData(it) }
                    )
                    CustomSwitch(
                        title = "Developer mode",
                        checked = state.devModeEnabled,
                        onCheckedChange = { viewModel.setDevModeEnabled(it) }
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomButton(
                        enabled = !state.appUrlInvalid,
                        modifier = Modifier,
                        onClick = { viewModel.launchAppOnClickListener() },
                        title = stringResource(R.string.launch_app_caption)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen(viewModel = HomeViewModel(Application()))
}
