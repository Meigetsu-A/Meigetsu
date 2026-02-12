package com.meigetsu.feature.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val player = ExoPlayer.Builder(context).build()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl = _currentUrl.asStateFlow()

    private val _introRange = MutableStateFlow<Pair<Long, Long>?>(Pair(10000L, 85000L)) // Simulated
    val introRange = _introRange.asStateFlow()

    init {
        val encodedUrl: String? = savedStateHandle["url"]
        encodedUrl?.let {
            val url = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            playVideo(url)
        }
    }

    init {
        // Update current position every second
        kotlinx.coroutines.MainScope().launch {
            while (true) {
                _currentPosition.value = player.currentPosition
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    fun playVideo(url: String) {
        _currentUrl.value = url
        com.meigetsu.core.common.DiscordRPC.updatePresence("Watching Anime", url)
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        player.playbackParameters = PlaybackParameters(speed)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
