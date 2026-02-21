package com.meigetsu.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*

@Composable
fun MediaDetailScreen(
    viewModel: MediaDetailViewModel,
    onBackClick: () -> Unit,
    onWatchClick: (String, String) -> Unit,
    onReadClick: (String, String) -> Unit
) {
    val media by viewModel.media.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val selectedSource by viewModel.selectedSource.collectAsState()
    val tintColor = remember { CardColors.random() }

    media?.let { m ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(tintColor)
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.BottomStart)) {
                        Text(m.title, style = Typography.headlineLarge)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            m.genres.forEach { genre ->
                                Text(genre, style = Typography.labelSmall, color = Color.White.copy(0.7f))
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd),
                        color = Color.Black.copy(0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "${m.score ?: 0}%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = Typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(m.description ?: "No description available.", style = Typography.bodyMedium)

                    Spacer(Modifier.height(24.dp))

                    Text("Select Source", style = Typography.labelLarge)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(viewModel.sources) { source ->
                            Surface(
                                modifier = Modifier.clickable { viewModel.selectSource(source) },
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedSource?.name == source.name) PrimaryText else Color.White.copy(0.05f)
                            ) {
                                Text(
                                    source.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = Typography.labelSmall,
                                    color = if (selectedSource?.name == source.name) Background else SecondaryText
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            onClick = { viewModel.toggleLibrary() },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("ADD TO LIBRARY", style = Typography.labelLarge)
                        }
                        OutlinedButton(
                            onClick = {
                                val sourceName = selectedSource?.name ?: "source"
                                if (m.type == MediaType.ANIME) onWatchClick(m.id, sourceName)
                                else onReadClick(m.id, sourceName)
                            },
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (m.type == MediaType.ANIME) "START WATCHING" else "START READING", style = Typography.labelLarge)
                        }
                    }
                }
            }

            if (m.type == MediaType.ANIME) {
                items(episodes) { ep ->
                    EpisodeRow(ep) { onWatchClick(m.id, selectedSource?.name ?: "source") }
                }
            } else {
                items(chapters) { ch ->
                    ChapterRow(ch) { onReadClick(m.id, selectedSource?.name ?: "source") }
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun EpisodeRow(episode: SourceEpisode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            episode.number.toString().padStart(2, '0'),
            style = Typography.titleLarge,
            fontSize = 18.sp,
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(episode.title ?: "Episode ${episode.number}", style = Typography.bodyLarge.copy(fontStyle = FontStyle.Italic))
            Text("24:00", style = Typography.labelSmall)
        }
    }
}

@Composable
fun ChapterRow(chapter: SourceChapter, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            chapter.number.toInt().toString().padStart(2, '0'),
            style = Typography.titleLarge,
            fontSize = 18.sp,
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(chapter.title ?: "Chapter ${chapter.number}", style = Typography.bodyLarge)
            Text(chapter.date ?: "Recently", style = Typography.labelSmall)
        }
    }
}
