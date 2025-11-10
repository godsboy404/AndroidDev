package com.example.player

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun rememberFilePicker(
    onAudioFilePicked: (Uri) -> Unit = {},
    onVideoFilePicked: (Uri) -> Unit = {}
): FilePicker {
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAudioFilePicked(it) }
    }
    
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onVideoFilePicked(it) }
    }
    
    return remember {
        FilePicker(
            pickAudioFile = { audioPickerLauncher.launch("audio/*") },
            pickVideoFile = { videoPickerLauncher.launch("video/*") }
        )
    }
}

class FilePicker(
    val pickAudioFile: () -> Unit,
    val pickVideoFile: () -> Unit
)