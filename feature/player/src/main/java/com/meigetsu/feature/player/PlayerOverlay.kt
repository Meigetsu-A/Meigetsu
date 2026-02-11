package com.meigetsu.feature.player

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isVisible) Color.Black.copy(alpha = 0.5f) else Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { isVisible = !isVisible },
                    onDoubleTap = { offset ->
                        if (offset.x < size.width / 2) onDoubleTapLeft()
                        else onDoubleTapRight()
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
        if (isVisible) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Episode Title", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Settings */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Center Controls
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDoubleTapLeft, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "Rewind", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = onPlayPause, modifier = Modifier.size(80.dp)) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))
                IconButton(onClick = onDoubleTapRight, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Default.FastForward, contentDescription = "Forward", tint = Color.White)
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter)
            ) {
                Slider(
                    value = 0.5f,
                    onValueChange = { /* Seek */ },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "00:00 / 24:00", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Row {
                        IconButton(onClick = { /* Subtitles */ }) {
                            Icon(Icons.Default.Info, contentDescription = "Subtitles", tint = Color.White)
                        }
                        IconButton(onClick = { /* Quality */ }) {
                            Icon(Icons.Default.Hd, contentDescription = "Quality", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}
