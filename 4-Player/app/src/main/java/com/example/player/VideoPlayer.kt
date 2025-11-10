package com.example.player

import android.content.Context
import android.view.SurfaceView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.player.ui.theme.PlayerTheme

@Composable
fun VideoPlayer(
    mediaPlayerManager: MediaPlayerManager,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            android.util.Log.d("VideoPlayer", "Creating SurfaceView")
            val surfaceView = SurfaceView(context)
            // 设置SurfaceView的回调
            surfaceView.holder.addCallback(object : android.view.SurfaceHolder.Callback {
                override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                    android.util.Log.d("VideoPlayer", "Surface created")
                    try {
                        mediaPlayerManager.setVideoSurface(surfaceView)
                    } catch (e: Exception) {
                        android.util.Log.e("VideoPlayer", "Error setting video surface", e)
                    }
                }
                
                override fun surfaceChanged(
                    holder: android.view.SurfaceHolder,
                    format: Int,
                    width: Int,
                    height: Int
                ) {
                    android.util.Log.d("VideoPlayer", "Surface changed: ${width}x$height, format: $format")
                }
                
                override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                    android.util.Log.d("VideoPlayer", "Surface destroyed")
                    try {
                        mediaPlayerManager.setVideoSurface(null)
                    } catch (e: Exception) {
                        android.util.Log.e("VideoPlayer", "Error clearing video surface", e)
                    }
                }
            })
            surfaceView
        },
        update = { surfaceView ->
            // 当组件更新时，确保表面设置正确
            android.util.Log.d("VideoPlayer", "Updating SurfaceView")
            try {
                mediaPlayerManager.setVideoSurface(surfaceView)
            } catch (e: Exception) {
                android.util.Log.e("VideoPlayer", "Error updating video surface", e)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f) // 16:9宽高比
    )
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerPreview() {
    PlayerTheme {
        // 在预览中显示一个占位符而不是实际的AndroidView
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "视频播放",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "视频播放区域",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "16:9 宽高比",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}