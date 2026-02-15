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
import com.meigetsu.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    globalSearchViewModel: GlobalSearchViewModel,
    onMediaClick: (String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    val searchResult by viewModel.searchResult.collectAsState()
    val characterSearchResult by viewModel.characterSearchResult.collectAsState()
    val globalResults by globalSearchViewModel.searchResults.collectAsState()
    val isGlobalLoading by globalSearchViewModel.isLoading.collectAsState()
    val availableExtensions by viewModel.availableExtensions.collectAsState()
    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }
    var searchType by remember { mutableStateOf("ANIME") }
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
                        icon = { Icon(Icons.Rounded.Source, null) },
                        text = { Text("Sources") }
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        icon = { Icon(Icons.Rounded.Extension, null) },
                        text = { Text("Extensions") }
                    )
                }
                if (activeTab == 0) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        FilterChip(
                            selected = searchType == "ANIME",
                            onClick = { searchType = "ANIME" },
                            label = { Text("Anime") }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = searchType == "CHARACTER",
                            onClick = { searchType = "CHARACTER" },
                            label = { Text("Characters") }
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = searchType == "GLOBAL",
                            onClick = { searchType = "GLOBAL" },
                            label = { Text("Global") }
                        )
                    }
                }
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        if (searchType == "GLOBAL") globalSearchViewModel.search(it)
                        else viewModel.search(it, searchType)
                    },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search $searchType...") },
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
            0 -> {
                when (searchType) {
                    "ANIME" -> AniListSearchResults(searchResult, innerPadding, onMediaClick)
                    "CHARACTER" -> CharacterSearchResults(characterSearchResult, innerPadding, onCharacterClick)
                    "GLOBAL" -> GlobalSearchResults(globalResults, isGlobalLoading, innerPadding, onMediaClick)
                }
            }
            1 -> ExtensionsList(availableExtensions, innerPadding) { viewModel.installExtension(it) }
        }
    }
}

@Composable
fun CharacterSearchResults(
    result: Resource<List<com.meigetsu.core.model.Character>>,
    innerPadding: PaddingValues,
    onCharacterClick: (String) -> Unit
) {
    when (result) {
        is Resource.Loading -> {
            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
                items(12) { LoadingSkeleton(Modifier.padding(4.dp)) }
            }
        }
        is Resource.Success -> {
            val characters = result.data ?: emptyList()
            if (characters.isEmpty()) {
                EmptyView(modifier = Modifier.padding(innerPadding))
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
                    items(characters) { char ->
                        MediaCard(
                            title = char.name,
                            imageUrl = char.image,
                            onClick = { onCharacterClick(char.id) }
                        )
                    }
                }
            }
        }
        is Resource.Error -> {
            ErrorView(message = result.message ?: "Search failed", modifier = Modifier.padding(innerPadding))
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
            val animeList = result.data ?: emptyList()
            if (animeList.isEmpty()) {
                EmptyView(modifier = Modifier.padding(innerPadding))
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(innerPadding)) {
                    items(animeList) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            type = anime.format.name,
                            onClick = { onMediaClick(anime.id) }
                        )
                    }
                }
            }
        }
        is Resource.Error -> {
            ErrorView(message = result.message ?: "Search failed", modifier = Modifier.padding(innerPadding))
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
    } else if (results.isEmpty()) {
        EmptyView(modifier = Modifier.padding(innerPadding))
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
