package com.meigetsu.feature.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.MediaCard
import com.meigetsu.core.ui.components.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String) -> Unit
) {
    val searchResult by viewModel.searchResult.collectAsState()
    var query by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = activeTab) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Search") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Extensions") })
            Tab(selected = activeTab == 2, onClick = { activeTab = 2 }, text = { Text("Migrate") })
        }

        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { viewModel.search(it) },
            active = false,
            onActiveChange = {},
            placeholder = { Text("Search Anime & Manga...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) { }

        when (val result = searchResult) {
            is Resource.Loading -> {
                LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
                    items(12) { LoadingSkeleton(Modifier.padding(4.dp)) }
                }
            }
            is Resource.Success -> {
                LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(8.dp)) {
                    items(result.data ?: emptyList()) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            type = anime.format.name,
                            onClick = { onMediaClick(anime.id) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            is Resource.Error -> {
                Text(text = result.message ?: "Search failed", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
