package com.meigetsu.feature.details
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
    val sources by viewModel.sources.collectAsState()
    val selectedSource by viewModel.selectedSource.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val isBookmarked by viewModel.isBookmarked.collectAsState()
    val sourceMediaId by viewModel.sourceMediaId.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().background(Background)) {
        item {
            Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                AsyncImage(model = media?.bannerImage ?: media?.coverImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Transparent, Background), startY = 100f)))
                IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp)) { Icon(Icons.Rounded.ArrowBack, null, tint = PrimaryText) }
                IconButton(onClick = { viewModel.toggleBookmark() }, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    Icon(if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder, null, tint = PrimaryText)
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(media?.title ?: "Loading...", style = Typography.headlineLarge)
                Spacer(Modifier.height(8.dp))
                Text(media?.description ?: "", style = Typography.bodyMedium, maxLines = 4)
                Spacer(Modifier.height(24.dp))
                SourceSelector(sources, selectedSource) { viewModel.selectSource(it) }
                Spacer(Modifier.height(24.dp))
                if (media?.type == MediaType.ANIME) {
                    Text("EPISODES (${episodes.size})", style = Typography.labelLarge)
                    if (sourceMediaId != null) {
                        Button(onClick = { onWatchClick(sourceMediaId!!, selectedSource?.name ?: "") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("WATCH NOW")
                        }
                    } else if (selectedSource != null) {
                        Text("Searching source...", style = Typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                    }
                } else {
                    Text("CHAPTERS (${chapters.size})", style = Typography.labelLarge)
                    if (sourceMediaId != null) {
                        Button(onClick = { onReadClick(sourceMediaId!!, selectedSource?.name ?: "") }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                            Text("READ NOW")
                        }
                    } else if (selectedSource != null) {
                        Text("Searching source...", style = Typography.labelSmall, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun SourceSelector(sources: List<Source>, selected: Source?, onSelect: (Source) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        sources.forEach { source ->
            val isSelected = source == selected
            Surface(
                modifier = Modifier.clickable { onSelect(source) },
                color = if (isSelected) PrimaryText.copy(0.1f) else Color.Transparent,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MutedText),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(source.name, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = Typography.labelSmall, color = if (isSelected) PrimaryText else MutedText)
            }
        }
    }
}
