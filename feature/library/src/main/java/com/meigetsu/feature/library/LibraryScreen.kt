package com.meigetsu.feature.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meigetsu.core.model.Download as MediaDownload
import com.meigetsu.core.ui.components.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onMediaClick: (String) -> Unit
) {
    val libraryAnime by viewModel.libraryAnime.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Library", fontWeight = FontWeight.Bold) }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Anime") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Manga") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Downloads") })
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0, 1 -> LibraryGrid(libraryAnime, innerPadding, onMediaClick)
            2 -> DownloadsList(viewModel, innerPadding)
        }
    }
}

@Composable
fun LibraryGrid(
    libraryAnime: List<com.meigetsu.core.model.Anime>,
    innerPadding: PaddingValues,
    onMediaClick: (String) -> Unit
) {
    if (libraryAnime.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Your library is empty", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Start adding some media!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(libraryAnime) { anime ->
                MediaCard(
                    title = anime.title,
                    imageUrl = anime.coverImage,
                    onClick = { onMediaClick(anime.id) }
                )
            }
        }
    }
}

@Composable
fun DownloadsList(
    viewModel: LibraryViewModel,
    innerPadding: PaddingValues
) {
    val downloads by viewModel.downloads.collectAsState()

    if (downloads.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            Text(text = "No downloads yet", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            items(downloads) { download: MediaDownload ->
                ListItem(
                    headlineContent = { Text(download.mediaTitle) },
                    supportingContent = { Text(download.itemTitle) },
                    leadingContent = { Icon(Icons.Rounded.Download, contentDescription = null) },
                    trailingContent = {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "${(download.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                            LinearProgressIndicator(progress = download.progress, modifier = Modifier.width(64.dp))
                        }
                    }
                )
            }
        }
    }
}
