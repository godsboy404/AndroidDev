package com.example.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.player.ui.theme.PlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerApp()
                }
            }
        }
    }
}

@Composable
fun PlayerApp() {
    val context = LocalContext.current
    val mediaPlayerManager = remember { 
        android.util.Log.d("MainActivity", "Creating MediaPlayerManager")
        MediaPlayerManager(context) 
    }
    val mediaType by mediaPlayerManager.mediaType.collectAsState()
    
    // 网络输入状态
    var showNetworkInput by remember { mutableStateOf(false) }
    
    val filePicker = rememberFilePicker(
        onAudioFilePicked = { uri ->
            android.util.Log.d("MainActivity", "Audio file picked: $uri")
            try {
                mediaPlayerManager.loadMedia(uri, MediaPlayerManager.MediaType.AUDIO)
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error loading audio file", e)
            }
        },
        onVideoFilePicked = { uri ->
            android.util.Log.d("MainActivity", "Video file picked: $uri")
            try {
                mediaPlayerManager.loadMedia(uri, MediaPlayerManager.MediaType.VIDEO)
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Error loading video file", e)
            }
        }
    )
    
    // 清理资源
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerManager.release()
        }
    }
    
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题
            Text(
                text = "媒体播放器",
                style = MaterialTheme.typography.headlineMedium
            )
            
            // 文件选择和网络输入按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            filePicker.pickAudioFile()
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error picking audio file", e)
                        }
                    }
                ) {
                    Text("选择音频")
                }
                
                Button(
                    onClick = {
                        try {
                            filePicker.pickVideoFile()
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error picking video file", e)
                        }
                    }
                ) {
                    Text("选择视频")
                }
                
                Button(
                    onClick = {
                        showNetworkInput = !showNetworkInput
                    }
                ) {
                    Text("网络播放")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 网络媒体输入界面
            if (showNetworkInput) {
                NetworkMediaInput(
                    onAudioUrlSubmit = { url ->
                        android.util.Log.d("MainActivity", "Audio URL submitted: $url")
                        try {
                            mediaPlayerManager.loadNetworkMedia(url, MediaPlayerManager.MediaType.AUDIO)
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error loading audio from network", e)
                        }
                    },
                    onVideoUrlSubmit = { url ->
                        android.util.Log.d("MainActivity", "Video URL submitted: $url")
                        try {
                            mediaPlayerManager.loadNetworkMedia(url, MediaPlayerManager.MediaType.VIDEO)
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Error loading video from network", e)
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // 媒体播放区域
            when (mediaType) {
                MediaPlayerManager.MediaType.AUDIO -> {
                    AudioPlayer()
                }
                MediaPlayerManager.MediaType.VIDEO -> {
                    VideoPlayer(mediaPlayerManager)
                }
                MediaPlayerManager.MediaType.NONE -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("请选择要播放的媒体文件或输入网络URL")
                    }
                }
            }
            
            // 播放控制
            PlayerControls(mediaPlayerManager)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerAppPreview() {
    PlayerTheme {
        PlayerApp()
    }
}