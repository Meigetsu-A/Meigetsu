package com.meigetsu.feature.player

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    var showControls by remember { mutableStateOf(true) }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (showControls) {
            PlayerControls(
                onBackClick = onBackClick,
                onTogglePlay = {
                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                },
                isPlaying = exoPlayer.isPlaying
            )
        }
    }
}

@Composable
fun PlayerControls(
    onBackClick: () -> Unit,
    onTogglePlay: () -> Unit,
    isPlaying: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Rounded.ArrowBack, null, tint = PrimaryText)
            }
            Text("Episode 12", style = Typography.labelLarge, color = PrimaryText)
            Text("GogoAnime", style = Typography.labelLarge, color = SecondaryText)
        }

        Spacer(Modifier.weight(1f))

        // Bottom Bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = 0.5f,
                onValueChange = {},
                colors = SliderDefaults.colors(thumbColor = SecondaryText, activeTrackColor = SecondaryText)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("12:00", style = Typography.labelSmall)
                Text("24:00", style = Typography.labelSmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) { Icon(Icons.Rounded.SkipPrevious, null, tint = PrimaryText) }
                IconButton(onClick = onTogglePlay, modifier = Modifier.size(64.dp)) {
                    Icon(
                        if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        null,
                        tint = PrimaryText,
                        modifier = Modifier.size(48.dp)
                    )
                }
                IconButton(onClick = {}) { Icon(Icons.Rounded.SkipNext, null, tint = PrimaryText) }
            }
        }
    }
}
