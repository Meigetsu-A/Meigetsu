package com.meigetsu.feature.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val player = viewModel.player
    var showControls by remember { mutableStateOf(true) }

    // Hide controls automatically after 3 seconds
    LaunchedEffect(showControls) {
        if (showControls) {
            kotlinx.coroutines.delay(3000)
            showControls = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val isPlaying by viewModel.isPlaying.collectAsState()
        val duration by viewModel.duration.collectAsState()

        if (duration == 0L) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectTapGestures(onTap = { showControls = !showControls })
            },
            update = { it.player = player }
        )

        // Modern Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            PlayerOverlay(
                viewModel = viewModel,
                onBackClick = onBackClick,
                onToggleControls = { showControls = !showControls }
            )
        }
    }
}

@Composable
fun PlayerOverlay(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit,
    onToggleControls: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
            Text("Playing Media", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { /* Settings */ }) {
                Icon(Icons.Rounded.Settings, null, tint = Color.White)
            }
        }

        // Center Controls
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.seekRelative(-10000) }, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Rounded.Replay10, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            val isPlaying by viewModel.isPlaying.collectAsState()
            FloatingActionButton(
                onClick = { if (isPlaying) viewModel.pause() else viewModel.play() },
                containerColor = Color.White,
                contentColor = Color.Black,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    null,
                    modifier = Modifier.size(40.dp)
                )
            }

            IconButton(onClick = { viewModel.seekRelative(10000) }, modifier = Modifier.size(64.dp)) {
                Icon(Icons.Rounded.Forward10, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            val progress by viewModel.progress.collectAsState()
            val duration by viewModel.duration.collectAsState()

            Slider(
                value = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f,
                onValueChange = { viewModel.seekTo((it * duration).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.Gray
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatTime(progress), color = Color.White, style = MaterialTheme.typography.labelMedium)
                Text(formatTime(duration), color = Color.White, style = MaterialTheme.typography.labelMedium)
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { /* Next Episode */ }) {
                    Icon(Icons.Rounded.SkipNext, null)
                    Spacer(Modifier.width(4.dp))
                    Text("NEXT EPISODE")
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
