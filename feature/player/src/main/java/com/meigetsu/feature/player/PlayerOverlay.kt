package com.meigetsu.feature.player

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerOverlay(
    isPlaying: Boolean,
    title: String,
    subtitle: String,
    currentPosition: Long,
    duration: Long,
    playbackSpeed: Float,
    onPlayPause: () -> Unit,
    onDoubleTapLeft: () -> Unit,
    onDoubleTapRight: () -> Unit,
    onBackClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onSpeedClick: () -> Unit,
    onEpisodesClick: () -> Unit,
    onQualityClick: () -> Unit,
    onSeek: (Float) -> Unit,
    showSkipIntro: Boolean = false,
    onSkipIntro: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    var seekPreviewTime by remember { mutableStateOf<String?>(null) }

    val overlayAlpha by animateFloatAsState(if (isVisible) 1f else 0f, label = "alpha")

    LaunchedEffect(isVisible) {
        if (isVisible) {
            kotlinx.coroutines.delay(5000)
            isVisible = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
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
        // Double tap feedback
        AnimatedVisibility(
            visible = seekPreviewTime != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = seekPreviewTime ?: "",
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black
            )
            LaunchedEffect(seekPreviewTime) {
                kotlinx.coroutines.delay(600)
                seekPreviewTime = null
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f * overlayAlpha)).alpha(overlayAlpha)) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(text = subtitle, color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onEpisodesClick) {
                    Icon(Icons.Rounded.PlaylistPlay, contentDescription = "Episodes", tint = Color.White)
                }
                IconButton(onClick = { /* Subtitles */ }) {
                    Icon(Icons.Rounded.Subtitles, contentDescription = "Subtitles", tint = Color.White)
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
                IconButton(onClick = onDoubleTapLeft, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Rounded.Replay10, contentDescription = "Rewind", tint = Color.White, modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.width(48.dp))
                IconButton(onClick = onPlayPause, modifier = Modifier.size(96.dp)) {
                    Icon(
                        if (isPlaying) Icons.Rounded.PauseCircleFilled else Icons.Rounded.PlayCircleFilled,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(96.dp)
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
                IconButton(onClick = onDoubleTapRight, modifier = Modifier.size(72.dp)) {
                    Icon(Icons.Rounded.Forward10, contentDescription = "Forward", tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }

            if (showSkipIntro) {
                Button(
                    onClick = onSkipIntro,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 120.dp, end = 32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("SKIP INTRO", fontWeight = FontWeight.Bold)
                }
            }

            // Bottom Controls
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.BottomCenter)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = onSeek,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${formatTime(currentPosition)} / ${formatTime(duration)}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${playbackSpeed}x",
                            modifier = Modifier.clickable { onSpeedClick() }.padding(horizontal = 12.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onQualityClick) {
                            Icon(Icons.Rounded.HighQuality, contentDescription = "Quality", tint = Color.White)
                        }
                        IconButton(onClick = { /* Lock */ }) {
                            Icon(Icons.Rounded.LockOpen, contentDescription = "Lock", tint = Color.White)
                        }
                        IconButton(onClick = { /* Fullscreen */ }) {
                            Icon(Icons.Rounded.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSecs = ms / 1000
    val hours = totalSecs / 3600
    val mins = (totalSecs % 3600) / 60
    val secs = totalSecs % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
