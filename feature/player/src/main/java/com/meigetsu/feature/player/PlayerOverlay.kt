package com.meigetsu.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PlayerOverlay(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onDoubleTapLeft: () -> Unit,
    onDoubleTapRight: () -> Unit,
    onBackClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onSpeedClick: () -> Unit,
    onEpisodesClick: () -> Unit,
    onExternalPlayerClick: () -> Unit,
    onSnapshotClick: () -> Unit = {},
    showSkipIntro: Boolean = false,
    onSkipIntro: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    var seekPreviewTime by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isVisible) Color.Black.copy(alpha = 0.5f) else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isVisible = !isVisible },
                    onDoubleTap = { offset ->
                        if (offset.x < size.width / 2) {
                            onDoubleTapLeft()
                            seekPreviewTime = "-10s"
                        } else {
                            onDoubleTapRight()
                            seekPreviewTime = "+10s"
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (change.position.x < size.width / 2) {
                        onBrightnessChange(-dragAmount / size.height)
                    } else {
                        onVolumeChange(-dragAmount / size.height)
                    }
                }
            }
    ) {
        if (seekPreviewTime != null) {
            Text(
                text = seekPreviewTime!!,
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            LaunchedEffect(seekPreviewTime) {
                kotlinx.coroutines.delay(500)
                seekPreviewTime = null
            }
        }

        if (isVisible) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Now Playing", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onSnapshotClick) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = "Take Snapshot", tint = Color.White)
                }
                IconButton(onClick = onExternalPlayerClick) {
                    Icon(Icons.Rounded.OpenInNew, contentDescription = "External Player", tint = Color.White)
                }
                IconButton(onClick = onEpisodesClick) {
                    Icon(Icons.Rounded.PlaylistPlay, contentDescription = "Episodes", tint = Color.White)
                }
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Center Controls
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDoubleTapLeft, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Rounded.Replay10, contentDescription = "Rewind", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = onPlayPause, modifier = Modifier.size(80.dp)) {
                    Icon(
                        if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = onDoubleTapRight, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Rounded.Forward10, contentDescription = "Forward", tint = Color.White)
                }
            }

            if (showSkipIntro) {
                Button(
                    onClick = onSkipIntro,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 100.dp, end = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.8f), contentColor = Color.Black),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Skip Intro")
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "1.0x", modifier = Modifier.clickable { onSpeedClick() }, color = Color.White, style = MaterialTheme.typography.labelLarge)
                    Spacer(modifier = Modifier.width(16.dp))
                    Slider(
                        value = 0.5f,
                        onValueChange = { /* Seek */ },
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "00:00 / 24:00", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Row {
                        IconButton(onClick = { /* Subtitles */ }) {
                            Icon(Icons.Rounded.Subtitles, contentDescription = "Subtitles", tint = Color.White)
                        }
                        IconButton(onClick = { /* Quality */ }) {
                            Icon(Icons.Rounded.HighQuality, contentDescription = "Quality", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
