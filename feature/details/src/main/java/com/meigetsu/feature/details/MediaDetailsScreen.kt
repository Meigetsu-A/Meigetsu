package com.meigetsu.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MediaDetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    onWatchClick: (String) -> Unit,
    onReadClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val streamUrls by viewModel.streamUrls.collectAsState()
    val uriHandler = LocalUriHandler.current
    val episodes by viewModel.episodes.collectAsState()
    val selectedIds by viewModel.selectedEpisodeIds.collectAsState()
    val isSelectionMode by viewModel.isSelectionMode.collectAsState()

    LaunchedEffect(streamUrls) {
        if (streamUrls.isNotEmpty()) {
            onWatchClick(streamUrls.first().url)
        }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    title = { Text("${selectedIds.size} Selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Close")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Select all logic */ }) {
                            Icon(Icons.Rounded.SelectAll, contentDescription = "Select All")
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        },
        bottomBar = {
            if (isSelectionMode) {
                BottomAppBar(
                    actions = {
                        Button(
                            onClick = { viewModel.downloadSelected() },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Rounded.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download (${selectedIds.size})")
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is Resource.Loading -> LoadingSkeleton(Modifier.fillMaxSize())
            is Resource.Success -> {
                val anime = state.data!!
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Box(modifier = Modifier.height(400.dp)) {
                            AsyncImage(
                                model = anime.bannerImage ?: anime.coverImage,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black)
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(24.dp)
                            ) {
                                Text(
                                    text = anime.title,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "${anime.year ?: ""} • ${anime.format.name} • ★ ${anime.rating ?: ""}", color = Color.LightGray)
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        if (anime.format.name == "MANGA") onReadClick(anime.id)
                                        else viewModel.fetchStreams()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(if (anime.format.name == "MANGA") Icons.Rounded.MenuBook else Icons.Rounded.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (anime.format.name == "MANGA") "Read" else "Watch")
                                }
                                val trailerUrl = anime.trailerUrl
                                if (trailerUrl != null) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    FilledTonalButton(
                                        onClick = { uriHandler.openUri(trailerUrl) },
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(Icons.Rounded.SmartDisplay, contentDescription = null)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                FilledTonalIconButton(
                                    onClick = { viewModel.addToLibrary() },
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Rounded.Add, contentDescription = "Add")
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = anime.description ?: "No description available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    item {
                        Text(
                            text = if (anime.format.name == "MANGA") "Chapters" else "Episodes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    items(episodes) { episode ->
                        val isSelected = selectedIds.contains(episode.id)
                        ListItem(
                            headlineContent = { Text(episode.title ?: "") },
                            supportingContent = { Text("${if (anime.format.name == "MANGA") "Chapter" else "Episode"} ${episode.number}") },
                            leadingContent = {
                                if (isSelectionMode) {
                                    Checkbox(checked = isSelected, onCheckedChange = { viewModel.toggleSelection(episode.id) })
                                } else {
                                    Icon(Icons.Rounded.PlayCircle, contentDescription = null)
                                }
                            },
                            trailingContent = {
                                IconButton(onClick = { /* Download single */ }) {
                                    Icon(Icons.Rounded.Download, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .combinedClickable(
                                    onClick = {
                                        if (isSelectionMode) viewModel.toggleSelection(episode.id)
                                        else viewModel.fetchStreams()
                                    },
                                    onLongClick = { viewModel.toggleSelection(episode.id) }
                                )
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        )
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Error loading details", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
