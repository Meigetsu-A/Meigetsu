package com.meigetsu.feature.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.model.Download as MediaDownload
import com.meigetsu.core.ui.components.MediaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onMediaClick: (String, String) -> Unit
) {
    val libraryAnime by viewModel.libraryAnime.collectAsState()
    val libraryManga by viewModel.libraryManga.collectAsState()
    val favoriteCharacters by viewModel.favoriteCharacters.collectAsState()
    val selectedIds by viewModel.selectedIds.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                CenterAlignedTopAppBar(
                    title = { Text(if (selectedIds.isEmpty()) "LIBRARY" else "${selectedIds.size} Selected", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp)) },
                    navigationIcon = {
                        if (selectedIds.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSelection() }) {
                                Icon(Icons.Rounded.Close, null)
                            }
                        }
                    },
                    actions = {
                        if (selectedIds.isNotEmpty()) {
                            IconButton(onClick = { viewModel.deleteSelected() }) {
                                Icon(Icons.Rounded.Delete, null)
                            }
                            IconButton(onClick = { /* Status Bottom Sheet */ }) {
                                Icon(Icons.Rounded.Edit, null)
                            }
                        } else {
                            IconButton(onClick = { /* Search library */ }) {
                                Icon(Icons.Rounded.Search, null)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {},
                    edgePadding = 16.dp
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Anime") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Manga") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Characters") })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Downloads") })
                    categories.forEachIndexed { index, category ->
                        Tab(selected = selectedTab == index + 4, onClick = { selectedTab = index + 4 }, text = { Text(category.name) })
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        when (selectedTab) {
            0 -> LibraryGrid(libraryAnime, selectedIds, viewModel, innerPadding, onMediaClick)
            1 -> LibraryMangaGrid(libraryManga, selectedIds, viewModel, innerPadding, onMediaClick)
            2 -> CharacterLibraryGrid(favoriteCharacters, innerPadding)
            3 -> DownloadsList(viewModel, innerPadding)
            else -> {
                LibraryGrid(libraryAnime, selectedIds, viewModel, innerPadding, onMediaClick)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryMangaGrid(
    libraryManga: List<com.meigetsu.core.model.Manga>,
    selectedIds: Set<String>,
    viewModel: LibraryViewModel,
    innerPadding: PaddingValues,
    onMediaClick: (String, String) -> Unit
) {
    if (libraryManga.isEmpty()) {
        EmptyLibraryView(innerPadding)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(libraryManga) { manga ->
                val isSelected = selectedIds.contains(manga.id)
                Box {
                    MediaCard(
                        title = manga.title,
                        imageUrl = manga.coverImage,
                        onClick = {
                            if (selectedIds.isNotEmpty()) viewModel.toggleSelection(manga.id)
                            else onMediaClick(manga.id, "MANGA")
                        },
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                if (selectedIds.isNotEmpty()) viewModel.toggleSelection(manga.id)
                                else onMediaClick(manga.id, "MANGA")
                            },
                            onLongClick = { viewModel.toggleSelection(manga.id) }
                        )
                    )
                    if (isSelected) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterLibraryGrid(
    characters: List<com.meigetsu.core.model.Character>,
    innerPadding: PaddingValues
) {
    if (characters.isEmpty()) {
        EmptyLibraryView(innerPadding)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(characters) { char ->
                MediaCard(
                    title = char.name,
                    imageUrl = char.image,
                    onClick = { /* Char details */ }
                )
            }
        }
    }
}

@Composable
fun EmptyLibraryView(innerPadding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Your library is empty", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Start adding some media!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryGrid(
    libraryAnime: List<com.meigetsu.core.model.Anime>,
    selectedIds: Set<String>,
    viewModel: LibraryViewModel,
    innerPadding: PaddingValues,
    onMediaClick: (String, String) -> Unit
) {
    if (libraryAnime.isEmpty()) {
        EmptyLibraryView(innerPadding)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(libraryAnime) { anime ->
                val isSelected = selectedIds.contains(anime.id)
                Box {
                    MediaCard(
                        title = anime.title,
                        imageUrl = anime.coverImage,
                        onClick = {
                            if (selectedIds.isNotEmpty()) viewModel.toggleSelection(anime.id)
                            else onMediaClick(anime.id, "ANIME")
                        },
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                if (selectedIds.isNotEmpty()) viewModel.toggleSelection(anime.id)
                                else onMediaClick(anime.id, "ANIME")
                            },
                            onLongClick = { viewModel.toggleSelection(anime.id) }
                        )
                    )
                    if (isSelected) {
                        Surface(
                            modifier = Modifier.matchParentSize(),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                }
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
