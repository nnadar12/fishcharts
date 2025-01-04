package com.example.fishcharts

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fishcharts.ui.theme.FishChartsTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box


import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import coil.compose.AsyncImage





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }


        setContent {
            FishChartsTheme {
                MainScreen(
                    onCapturePhoto = {
                        
                    }, //Will add camera functionality later
                    onSaveLocation = {

                    },
                    onSaveRecord = { photoUri, weight, location ->
                        //
                    },
                    records = listOf(/*
                        FishRecord(weight = "2.5", location = "Lake Tahoe"),
                        FishRecord(weight = "3.8", location = "Mississippi River")*/
                    )
                )
            }
        }
    }
}

data class FishRecord(
    val weight: String,
    val location: String,
    val photoUri: String

)


@Composable
fun CameraPreview(onCapture: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val preview = remember { Preview.Builder().build() }
    val cameraSelector = remember { CameraSelector.DEFAULT_BACK_CAMERA }
    val imageCapture = remember { ImageCapture.Builder().build() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                val photoFile = File(
                    context.externalMediaDirs.first(),
                    "${System.currentTimeMillis()}.jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            val savedUri = photoFile.absolutePath
                            onCapture(savedUri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Capture")
        }
    }
}



@Composable
fun MainScreen(
    onCapturePhoto: () -> Unit,
               onSaveLocation: () -> Unit,
               onSaveRecord: (String, String, String) -> Unit,
               records: List<FishRecord>
) {
    var records by remember { mutableStateOf(listOf<FishRecord>()) }
    var weight by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        CameraPreview(onCapture = {
            photoUri = it
            showCamera = false
        })
    } else {
        Column(modifier = Modifier.padding(16.dp, 16.dp)) {
            Button(onClick = { showCamera = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Take Picture")
            }

            TextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Enter Weight (lbs)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = onSaveLocation, modifier = Modifier.fillMaxWidth()) {
                Text("Save Location")
            }

            Button(
                onClick = {
                    // Create a new FishRecord and add it to the list
                    val newRecord = FishRecord(weight, "Location Placeholder", photoUri)
                    records = records + newRecord  // Add new record to the list
                    onSaveRecord(photoUri, weight, "Location Placeholder")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Log Catch")
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn {
                items(records) { record ->
                    Text("Fish: ${record.weight} lbs at ${record.location}")
                    Spacer(modifier = Modifier.height(8.dp))
                    AsyncImage(
                        model = record.photoUri,
                        contentDescription = "Image of caught fish",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }


        }
    }
}


