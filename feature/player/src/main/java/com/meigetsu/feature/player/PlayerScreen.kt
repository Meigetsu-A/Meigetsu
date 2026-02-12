package com.meigetsu.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var showEpisodeSheet by remember { mutableStateOf(false) }
    val currentUrl by viewModel.currentUrl.collectAsState()
    val currentPos by viewModel.currentPosition.collectAsState()
    val introRange by viewModel.introRange.collectAsState()
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
                viewModel.player.volume = (viewModel.player.volume + delta).coerceIn(0f, 1f)
            },
            onBrightnessChange = { /* Need Context/Activity for brightness */ },
            onSpeedClick = { /* Show speed menu */ },
            onEpisodesClick = { showEpisodeSheet = true },
            showSkipIntro = introRange != null && currentPos in introRange!!.first..introRange!!.second,
            onSkipIntro = { introRange?.let { viewModel.player.seekTo(it.second) } },
            onExternalPlayerClick = {
                currentUrl?.let { url ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                    intent.setDataAndType(android.net.Uri.parse(url), "video/*")
                    context.startActivity(intent)
                }
            }
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
