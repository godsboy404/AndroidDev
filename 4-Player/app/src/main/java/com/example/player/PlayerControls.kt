package com.example.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.player.ui.theme.PlayerTheme

@Composable
fun PlayerControls(
    mediaPlayerManager: MediaPlayerManager,
    modifier: Modifier = Modifier
) {
    val isPlaying by mediaPlayerManager.isPlaying.collectAsState()
    val currentPosition by mediaPlayerManager.currentPosition.collectAsState()
    val duration by mediaPlayerManager.duration.collectAsState()
    val mediaType by mediaPlayerManager.mediaType.collectAsState()
    
    var progress by remember(duration) {
        mutableFloatStateOf(if (duration > 0) currentPosition.toFloat() / duration else 0f)
    }
    
    // 更新进度
    LaunchedEffect(currentPosition, duration) {
        if (duration > 0) {
            progress = currentPosition.toFloat() / duration
        }
    }
    
    // 定期更新播放进度
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (mediaPlayerManager.isPlaying.value) {
                delay(500) // 每500毫秒更新一次
                mediaPlayerManager.updateProgress()
            }
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 媒体类型显示
        if (mediaType != MediaPlayerManager.MediaType.NONE) {
            Text(
                text = when (mediaType) {
                    MediaPlayerManager.MediaType.AUDIO -> "音频播放器"
                    MediaPlayerManager.MediaType.VIDEO -> "视频播放器"
                    else -> ""
                }
            )
        }
        
        // 进度条
        if (duration > 0) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = progress,
                    onValueChange = { newProgress ->
                        progress = newProgress
                        if (duration > 0) {
                            mediaPlayerManager.seekTo((newProgress * duration).toLong())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = mediaPlayerManager.getCurrentPositionFormatted())
                    Text(text = mediaPlayerManager.getDurationFormatted())
                }
            }
        } else {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color.Gray
            )
        }
        
        // 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 播放/暂停按钮
            Button(
                onClick = {
                    if (isPlaying) {
                        mediaPlayerManager.pause()
                    } else {
                        mediaPlayerManager.play()
                    }
                },
                enabled = mediaType != MediaPlayerManager.MediaType.NONE
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "暂停" else "播放",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // 停止按钮
            Button(
                onClick = { mediaPlayerManager.stop() },
                enabled = mediaType != MediaPlayerManager.MediaType.NONE
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "停止",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerControlsPreview() {
    PlayerTheme {
        // 预览中不创建MediaPlayerManager，只显示UI
        // 实际功能需要在真实设备上测试
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 媒体类型显示
            Text(
                text = "音频播放器"
            )
            
            // 进度条
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Slider(
                    value = 0.3f,
                    onValueChange = { },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "01:23")
                    Text(text = "04:56")
                }
            }
            
            // 控制按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 播放/暂停按钮
                Button(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "播放",
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // 停止按钮
                Button(
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "停止",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}