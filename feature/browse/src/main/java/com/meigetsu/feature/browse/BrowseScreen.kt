package com.meigetsu.feature.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.extensions.ExtensionRemote
import com.meigetsu.core.extensions.MediaSearchResult
import com.meigetsu.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    val animeResults by viewModel.animeResults.collectAsState()
    val mangaResults by viewModel.mangaResults.collectAsState()
    val characterResults by viewModel.characterResults.collectAsState()
    val extensionResults by viewModel.extensionResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val availableExtensions by viewModel.availableExtensions.collectAsState()

    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BrowseUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TabRow(selectedTabIndex = activeTab) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        icon = { Icon(Icons.Rounded.Search, null) },
                        text = { Text("Search") }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Rounded.Extension, null) },
                        text = { Text("Extensions") }
                    )
                }

                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { viewModel.search(it) },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search Anime, Manga, Characters...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) { }
            }
        }
    ) { innerPadding ->
        when (activeTab) {
            0 -> UnifiedSearchResults(
                animeResults,
                mangaResults,
                characterResults,
                extensionResults,
                isSearching,
                innerPadding,
                onMediaClick,
                onCharacterClick
            )
            1 -> ExtensionsList(availableExtensions, innerPadding) { viewModel.installExtension(it) }
        }
    }
}

@Composable
fun UnifiedSearchResults(
    anime: Resource<List<com.meigetsu.core.model.Anime>>,
    manga: Resource<List<com.meigetsu.core.model.Manga>>,
    characters: Resource<List<com.meigetsu.core.model.Character>>,
    extensions: List<MediaSearchResult>,
    isSearching: Boolean,
    innerPadding: PaddingValues,
    onMediaClick: (String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    if (isSearching) {
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        // Anime Section
        if (anime is Resource.Success && !anime.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Anime Results") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(anime.data!!) { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.coverImage,
                            modifier = Modifier.width(140.dp),
                            onClick = { onMediaClick(item.id) }
                        )
                    }
                }
            }
        }

        // Manga Section
        if (manga is Resource.Success && !manga.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Manga Results") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(manga.data!!) { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.coverImage,
                            modifier = Modifier.width(140.dp),
                            onClick = { onMediaClick(item.id) }
                        )
                    }
                }
            }
        }

        // Characters Section
        if (characters is Resource.Success && !characters.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Characters") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(characters.data!!) { item ->
                        MediaCard(
                            title = item.name,
                            imageUrl = item.image,
                            modifier = Modifier.width(140.dp),
                            onClick = { onCharacterClick(item.id) }
                        )
                    }
                }
            }
        }

        // Extension Results
        if (extensions.isNotEmpty()) {
            item { SearchSectionHeader("Extension Sources") }
            items(extensions.chunked(3)) { chunk ->
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    chunk.forEach { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.imageUrl,
                            type = item.type,
                            modifier = Modifier.weight(1f).padding(4.dp),
                            onClick = {
                                if (item.type.uppercase() == "CHARACTER") {
                                    onCharacterClick(item.id)
                                } else {
                                    onMediaClick(item.id)
                                }
                            }
                        )
                    }
                    if (chunk.size < 3) {
                        repeat(3 - chunk.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun ExtensionsList(
    extensions: List<ExtensionRemote>,
    innerPadding: PaddingValues,
    onInstall: (ExtensionRemote) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        items(extensions) { extension ->
            ListItem(
                headlineContent = { Text(extension.name) },
                supportingContent = { Text("${extension.pkg} • v${extension.version}") },
                leadingContent = {
                    AsyncImage(
                        model = extension.icon,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                },
                trailingContent = {
                    Button(onClick = { onInstall(extension) }) {
                        Text("Install")
                    }
                }
            )
        }
    }
}
