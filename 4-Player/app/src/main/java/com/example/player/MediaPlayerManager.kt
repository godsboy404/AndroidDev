package com.example.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    
    // 播放状态
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    // 当前播放位置
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition
    
    // 媒体总时长
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration
    
    // 当前媒体类型
    private val _mediaType = MutableStateFlow(MediaType.NONE)
    val mediaType: StateFlow<MediaType> = _mediaType
    
    // 当前媒体URI
    private var currentUri: Uri? = null
    
    enum class MediaType {
        NONE, AUDIO, VIDEO
    }
    
    init {
        initializeMediaPlayer()
    }
    
    private fun initializeMediaPlayer() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setOnPreparedListener {
                    android.util.Log.d("MediaPlayerManager", "Media prepared, duration: $duration")
                    _duration.value = duration.toLong()
                    // 不自动开始播放，让用户手动控制
                    _isPlaying.value = false
                }
                
                setOnCompletionListener {
                    android.util.Log.d("MediaPlayerManager", "Media playback completed")
                    _isPlaying.value = false
                    _currentPosition.value = 0L
                }
                
                setOnErrorListener { _, what, extra ->
                    android.util.Log.e("MediaPlayerManager", "Media error: what=$what, extra=$extra")
                    _isPlaying.value = false
                    _mediaType.value = MediaType.NONE
                    false
                }
                
                setOnVideoSizeChangedListener { _, width, height ->
                    android.util.Log.d("MediaPlayerManager", "Video size changed: ${width}x$height")
                }
                
                setOnInfoListener { _, what, extra ->
                    when (what) {
                        MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                            android.util.Log.d("MediaPlayerManager", "Video rendering started")
                        }
                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            android.util.Log.d("MediaPlayerManager", "Buffering started")
                        }
                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            android.util.Log.d("MediaPlayerManager", "Buffering ended")
                        }
                    }
                    true
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("MediaPlayerManager", "Error initializing MediaPlayer", e)
            e.printStackTrace()
        }
    }
    
    fun setVideoSurface(surfaceView: SurfaceView?) {
        mediaPlayer?.setSurface(surfaceView?.holder?.surface)
    }
    
    fun loadMedia(uri: Uri, mediaType: MediaType) {
        try {
            android.util.Log.d("MediaPlayerManager", "Loading media: $uri, type: $mediaType")
            
            currentUri = uri
            _mediaType.value = mediaType
            
            // 重置媒体播放器
            mediaPlayer?.reset()
            
            // 设置数据源 - 支持本地和网络URI
            if (uri.scheme == "http" || uri.scheme == "https") {
                // 网络URL
                android.util.Log.d("MediaPlayerManager", "Loading network media")
                mediaPlayer?.setDataSource(uri.toString())
            } else {
                // 本地文件
                android.util.Log.d("MediaPlayerManager", "Loading local media")
                mediaPlayer?.setDataSource(context, uri)
            }
            
            // 对于视频，确保在设置数据源后设置表面
            if (mediaType == MediaType.VIDEO) {
                android.util.Log.d("MediaPlayerManager", "Video media loaded, waiting for surface")
                // 表面会在VideoPlayer组件中设置
            }
            
            // 异步准备媒体
            mediaPlayer?.prepareAsync()
            
            android.util.Log.d("MediaPlayerManager", "Media preparation started")
        } catch (e: Exception) {
            android.util.Log.e("MediaPlayerManager", "Error loading media", e)
            e.printStackTrace()
            // 发生错误时重置媒体类型
            _mediaType.value = MediaType.NONE
        }
    }
    
    fun loadNetworkMedia(url: String, mediaType: MediaType) {
        try {
            android.util.Log.d("MediaPlayerManager", "Loading network media: $url, type: $mediaType")
            
            val uri = Uri.parse(url)
            loadMedia(uri, mediaType)
        } catch (e: Exception) {
            android.util.Log.e("MediaPlayerManager", "Error loading network media", e)
            e.printStackTrace()
            // 发生错误时重置媒体类型
            _mediaType.value = MediaType.NONE
        }
    }
    
    fun play() {
        try {
            mediaPlayer?.let { player ->
                if (!player.isPlaying) {
                    player.start()
                    _isPlaying.value = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun pause() {
        try {
            mediaPlayer?.pause()
            _isPlaying.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stop() {
        try {
            mediaPlayer?.stop()
            _isPlaying.value = false
            _currentPosition.value = 0L
            try {
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
                initializeMediaPlayer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun seekTo(position: Long) {
        try {
            mediaPlayer?.seekTo(position.toInt())
            // 不立即更新_currentPosition，让进度更新通过定期更新来处理
            // 这样可以避免拖曳时的跳动
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun startProgressUpdate() {
        // 进度更新现在在PlayerControls中处理
    }
    
    fun updateProgress() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                _currentPosition.value = player.currentPosition.toLong()
            }
        }
    }
    
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    fun getDurationFormatted(): String {
        val duration = _duration.value
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    fun getCurrentPositionFormatted(): String {
        val position = _currentPosition.value
        val minutes = (position / 1000) / 60
        val seconds = (position / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}