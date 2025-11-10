package com.example.weightcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StandardWeightCalculator()
        }
    }
}

@Composable
fun StandardWeightCalculator() {
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select your gender:", fontSize = 20.sp)
        Row {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gender == "Male",
                    onClick = { gender = "Male" }
                )
                Text("Male", modifier = Modifier.padding(end = 16.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = gender == "Female",
                    onClick = { gender = "Female" }
                )
                Text("Female")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Enter your height (cm)") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val h = height.toFloatOrNull()
            result = if (h != null) {
                val weight = if (gender == "Male") {
                    (h - 80) * 0.7
                } else {
                    (h - 70) * 0.6
                }
                "Standard weight: %.2f kg".format(weight)
            } else {
                "Please enter a valid height."
            }
        }) {
            Text("View Result")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(result, fontSize = 22.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun WeightCalcPreview() {
    StandardWeightCalculator()
}