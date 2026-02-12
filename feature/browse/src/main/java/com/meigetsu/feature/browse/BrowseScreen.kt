package com.meigetsu.feature.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.extensions.ExtensionRemote
import com.meigetsu.core.extensions.MediaSearchResult
import com.meigetsu.core.ui.components.MediaCard
import com.meigetsu.core.ui.components.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    globalSearchViewModel: GlobalSearchViewModel,
    onMediaClick: (String) -> Unit
) {
    val searchResult by viewModel.searchResult.collectAsState()
    val globalResults by globalSearchViewModel.searchResults.collectAsState()
    val isGlobalLoading by globalSearchViewModel.isLoading.collectAsState()
    val availableExtensions by viewModel.availableExtensions.collectAsState()
    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TabRow(selectedTabIndex = activeTab) {
                    Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("AniList") })
                    Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Global") })
                    Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Extensions") })
                }
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        if (activeTab == 0) viewModel.search(it)
                        else globalSearchViewModel.search(it)
                    },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text(if (activeTab == 0) "Search AniList..." else "Search all extensions...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { /* Start Voice Intent */ }) {
                            Icon(Icons.Rounded.Mic, contentDescription = "Voice Search")
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) { }
            }
        }
    ) { innerPadding ->
        when (activeTab) {
            0 -> AniListSearchResults(searchResult, innerPadding, onMediaClick)
            1 -> GlobalSearchResults(globalResults, isGlobalLoading, innerPadding, onMediaClick)
            2 -> ExtensionsList(availableExtensions, innerPadding) { viewModel.installExtension(it) }
        }
    }
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

@Composable
fun AniListSearchResults(
    result: Resource<List<com.meigetsu.core.model.Anime>>,
    innerPadding: PaddingValues,
    onMediaClick: (String) -> Unit
) {
    when (result) {
        is Resource.Loading -> {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
                items(12) { LoadingSkeleton(Modifier.padding(4.dp)) }
            }
        }
        is Resource.Success -> {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
                items(result.data ?: emptyList()) { anime ->
                    MediaCard(
                        title = anime.title,
                        imageUrl = anime.coverImage,
                        type = anime.format.name,
                        onClick = { onMediaClick(anime.id) }
                    )
                }
            }
        }
        is Resource.Error -> {
            Text(text = result.message ?: "Error", modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun GlobalSearchResults(
    results: List<MediaSearchResult>,
    isLoading: Boolean,
    innerPadding: PaddingValues,
    onMediaClick: (String) -> Unit
) {
    if (isLoading) {
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
            items(12) { LoadingSkeleton(Modifier.padding(4.dp)) }
        }
    } else {
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
            items(results) { item ->
                MediaCard(
                    title = item.title,
                    imageUrl = item.imageUrl,
                    type = item.type,
                    onClick = { onMediaClick(item.id) }
                )
            }
        }
    }
}
