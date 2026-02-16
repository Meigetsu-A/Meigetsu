package com.meigetsu.feature.player

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import com.meigetsu.core.extensions.StreamUrl
import com.meigetsu.core.model.Episode

import com.meigetsu.core.domain.repository.LibraryRepository

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val player = ExoPlayer.Builder(context).build()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0L)
    val progress = _progress.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed = _playbackSpeed.asStateFlow()

    private val _currentUrl = MutableStateFlow<String?>(null)
    val currentUrl = _currentUrl.asStateFlow()

    private val _introRange = MutableStateFlow<Pair<Long, Long>?>(Pair(10000L, 85000L)) // Simulated
    val introRange = _introRange.asStateFlow()

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    private val _availableQualities = MutableStateFlow<List<StreamUrl>>(emptyList())
    val availableQualities = _availableQualities.asStateFlow()

    private val _selectedQuality = MutableStateFlow<StreamUrl?>(null)
    val selectedQuality = _selectedQuality.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    _duration.value = player.duration
                }
            }
        })

        val encodedUrl: String? = savedStateHandle["url"]
        val mediaId: String? = savedStateHandle["mediaId"]

        encodedUrl?.let {
            val url = URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            playVideo(url)

            // Resume logic
            mediaId?.let { id ->
                viewModelScope.launch {
                    val history = libraryRepository.getWatchHistoryById(id)
                    history?.let {
                        if (it.position > 0 && it.position < it.duration - 10000) {
                            player.seekTo(it.position)
                        }
                    }
                }
            }
        }
    }

    init {
        // Update current position every second and save to history
        viewModelScope.launch {
            while (true) {
                val pos = player.currentPosition
                val dur = player.duration
                _progress.value = pos
                _currentPosition.value = pos

                val mediaId: String? = savedStateHandle["mediaId"]
                if (mediaId != null && pos > 0 && dur > 0) {
                    libraryRepository.updateWatchHistory(mediaId, 1, pos, dur)
                }

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

    fun play() = player.play()
    fun pause() = player.pause()
    fun seekTo(pos: Long) = player.seekTo(pos)
    fun seekRelative(ms: Long) = player.seekTo(player.currentPosition + ms)

    fun switchQuality(streamUrl: StreamUrl) {
        val currentPos = player.currentPosition
        _selectedQuality.value = streamUrl
        _currentUrl.value = streamUrl.url
        val mediaItem = MediaItem.fromUri(streamUrl.url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.seekTo(currentPos)
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
