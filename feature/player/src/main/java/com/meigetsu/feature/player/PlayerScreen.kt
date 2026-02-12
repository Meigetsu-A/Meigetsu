package com.meigetsu.feature.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showEpisodeSheet by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(1f) }
    var brightness by remember { mutableStateOf(1f) }

    Box(modifier = Modifier.fillMaxSize()) {
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
            onPlayPause = {
                if (isPlaying) viewModel.player.pause() else viewModel.player.play()
                isPlaying = !isPlaying
            },
            onDoubleTapLeft = {
                val currentPos = viewModel.player.currentPosition
                viewModel.player.seekTo(maxOf(0, currentPos - 10000))
            },
            onDoubleTapRight = {
                val currentPos = viewModel.player.currentPosition
                viewModel.player.seekTo(currentPos + 10000)
            },
            onBackClick = onBackClick,
            onVolumeChange = { delta ->
                volume = (volume + delta).coerceIn(0f, 1f)
                viewModel.player.volume = volume
            },
            onBrightnessChange = { delta ->
                brightness = (brightness + delta).coerceIn(0f, 1f)
                // In real app: update Activity window brightness
            },
            onSpeedClick = { /* Show speed menu */ },
            onEpisodesClick = { showEpisodeSheet = true }
        )

        if (showEpisodeSheet) {
            EpisodeBottomSheet(
                episodes = emptyList(),
                onEpisodeClick = { /* Change episode */ },
                onDismiss = { showEpisodeSheet = false }
            )
        }
    }
}
