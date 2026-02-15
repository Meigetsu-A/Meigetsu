package com.meigetsu.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showEpisodeSheet by remember { mutableStateOf(false) }
    var showQualitySheet by remember { mutableStateOf(false) }

    val currentUrl by viewModel.currentUrl.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val duration = viewModel.player.duration.let { if (it < 0) 0L else it }
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val introRange by viewModel.introRange.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val qualities by viewModel.availableQualities.collectAsState()

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.player
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        PlayerOverlay(
            isPlaying = isPlaying,
            title = "Episode 1", // Should come from media
            subtitle = "Meigetsu Player",
            currentPosition = currentPos,
            duration = duration,
            playbackSpeed = playbackSpeed,
            onPlayPause = {
                if (isPlaying) viewModel.player.pause() else viewModel.player.play()
                isPlaying = !isPlaying
            },
            onDoubleTapLeft = {
                viewModel.player.seekTo(maxOf(0, viewModel.player.currentPosition - 10000))
            },
            onDoubleTapRight = {
                viewModel.player.seekTo(viewModel.player.currentPosition + 10000)
            },
            onBackClick = onBackClick,
            onVolumeChange = { delta ->
                viewModel.player.volume = (viewModel.player.volume + delta).coerceIn(0f, 1f)
            },
            onBrightnessChange = { delta ->
                val activity = context as? android.app.Activity
                val attributes = activity?.window?.attributes
                if (attributes != null) {
                    val current = if (attributes.screenBrightness < 0) 0.5f else attributes.screenBrightness
                    attributes.screenBrightness = (current + delta).coerceIn(0.01f, 1f)
                    activity.window.attributes = attributes
                }
            },
            onSpeedClick = {
                val nextSpeed = when(playbackSpeed) {
                    1.0f -> 1.25f
                    1.25f -> 1.5f
                    1.5f -> 2.0f
                    else -> 1.0f
                }
                viewModel.setPlaybackSpeed(nextSpeed)
            },
            onEpisodesClick = { showEpisodeSheet = true },
            onQualityClick = { showQualitySheet = true },
            onSeek = { fraction ->
                viewModel.player.seekTo((fraction * duration).toLong())
            },
            showSkipIntro = introRange != null && currentPos in introRange!!.first..introRange!!.second,
            onSkipIntro = { introRange?.let { viewModel.player.seekTo(it.second) } }
        )

        if (showEpisodeSheet) {
            EpisodeBottomSheet(
                episodes = episodes,
                onEpisodeClick = { /* viewModel.loadEpisode(it) */ },
                onDismiss = { showEpisodeSheet = false }
            )
        }

        if (showQualitySheet) {
            ModalBottomSheet(onDismissRequest = { showQualitySheet = false }) {
                androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    items(qualities) { quality ->
                        ListItem(
                            headlineContent = { Text(quality.quality) },
                            modifier = Modifier.clickable {
                                viewModel.switchQuality(quality)
                                showQualitySheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}
