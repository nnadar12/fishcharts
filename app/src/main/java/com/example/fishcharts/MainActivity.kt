package com.example.fishcharts

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fishcharts.ui.theme.FishChartsTheme
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FishChartsTheme {
                MainScreen(
                    onCapturePhoto = { }, //Will add camera functionality later
                    onSaveLocation = { },
                    onSaveRecord = { photoUri, weight, location ->
                        //
                    },
                    records = listOf(
                        FishRecord(weight = "2.5", location = "Lake Tahoe"),
                        FishRecord(weight = "3.8", location = "Mississippi River")
                    )
                )
            }
        }
    }
}

data class FishRecord(
    val weight: String,
    val location: String
)




@Composable
fun MainScreen(
    onCapturePhoto: () -> Unit,
               onSaveLocation: () -> Unit,
               onSaveRecord: (String, String, String) -> Unit,
               records: List<FishRecord>
) {

    var weight by remember { mutableStateOf("") }
    val photoUri by remember { mutableStateOf("") }


    Column (modifier = Modifier.padding(16.dp, 16.dp)){
        Button(onClick = onCapturePhoto, modifier = Modifier.fillMaxWidth()) {
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
            onClick = { onSaveRecord(photoUri, weight, "Location Placeholder") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log Catch")
        }

        Spacer(modifier = Modifier.height(16.dp))


        LazyColumn  {
            items(records) { record ->
                Text("Fish: ${record.weight} lbs at ${record.location}")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }


    }

}


