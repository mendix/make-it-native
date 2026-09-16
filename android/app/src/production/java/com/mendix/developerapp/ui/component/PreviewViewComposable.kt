package com.mendix.developerapp.ui.component

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.mendix.developerapp.util.BarcodeAnalyser
import java.util.concurrent.Executors

/**
 * Keeps track of the camera bound by [PreviewViewComposable] so its use cases can be released when
 * the composable leaves the composition. Only touched from the main thread: the camera provider
 * listener runs on the main executor and so does composition disposal.
 */
private class CameraBinding {
    var provider: ProcessCameraProvider? = null
    var disposed = false
}

@androidx.camera.core.ExperimentalGetImage
@Composable
fun PreviewViewComposable(onSuccess: (String)->Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val binding = remember { CameraBinding() }

    /**
     * The QR scanner is conditional content inside the home screen rather than its own screen, so
     * nothing tears the camera down when it is dismissed. Without this the analyser keeps scanning
     * frames and reporting them for a screen the user already left.
     */
    DisposableEffect(Unit) {
        onDispose {
            binding.disposed = true
            binding.provider?.unbindAll()
            binding.provider = null
            cameraExecutor.shutdown()
        }
    }

    AndroidView({ context ->
        val previewView = PreviewView(context).also {
            it.scaleType = PreviewView.ScaleType.FILL_CENTER
        }
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // The provider is resolved asynchronously, so we may already have been disposed by the
            // time this runs. Binding now would leave a camera nothing unbinds again.
            if (binding.disposed) return@addListener

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor, BarcodeAnalyser{
                        val url = it.url!!.url
                        if(!url.isNullOrBlank()) {
                            onSuccess(url)
                        }
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to the composable's lifecycle rather than the activity's, so the
                // camera is released with the screen that owns it.
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageCapture, imageAnalyzer)
                binding.provider = cameraProvider

            } catch(exc: Exception) {
                Log.e("DEBUG", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(context))
        previewView
    },
        modifier = Modifier.fillMaxSize()
    )
}
