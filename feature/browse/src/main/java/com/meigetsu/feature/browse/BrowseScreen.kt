package com.meigetsu.feature.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.meigetsu.core.extensions.MediaSearchResult
import com.meigetsu.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String, String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    val animeResults by viewModel.animeResults.collectAsState()
    val mangaResults by viewModel.mangaResults.collectAsState()
    val characterResults by viewModel.characterResults.collectAsState()
    val extensionResults by viewModel.extensionResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    var query by remember { mutableStateOf("") }
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
            Column(modifier = Modifier.background(Color.Black)) {
                CenterAlignedTopAppBar(
                    title = { Text("SEARCH", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black)) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { viewModel.search(it) },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Anime, Manga, Characters...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = SearchBarDefaults.colors(containerColor = Color(0xFF141414))
                ) { }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        UnifiedSearchResults(
            animeResults,
            mangaResults,
            characterResults,
            extensionResults,
            isSearching,
            innerPadding,
            onMediaClick,
            onCharacterClick
        )
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
    onMediaClick: (String, String) -> Unit,
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
                            onClick = { onMediaClick(item.id, "ANIME") }
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
                            onClick = { onMediaClick(item.id, "MANGA") }
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
            item { SearchSectionHeader("Other Sources") }
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
                                    onMediaClick(item.id, item.type.uppercase())
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

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )
}
