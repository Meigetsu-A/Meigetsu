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

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.player
                    useController = false // We use our custom overlay
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
            onBackClick = onBackClick
        )
    }
}
