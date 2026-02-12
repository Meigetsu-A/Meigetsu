package com.meigetsu.feature.browse

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.MediaCard
import com.meigetsu.core.ui.components.LoadingSkeleton

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String) -> Unit
) {
    val searchResult by viewModel.searchResult.collectAsState()
    var query by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            TextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.search(it)
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search Anime...") }
            )
            IconButton(onClick = { showFilters = !showFilters }) {
                // Filter icon would go here
                Text("F")
            }
        }

        if (showFilters) {
            // Advanced Filters UI
            Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                AssistChip(onClick = { }, label = { Text("Genre") })
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(onClick = { }, label = { Text("Year") })
                Spacer(modifier = Modifier.width(8.dp))
                AssistChip(onClick = { }, label = { Text("Format") })
            }
        }

        when (val result = searchResult) {
            is Resource.Loading -> {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(9) { LoadingSkeleton(Modifier.padding(4.dp)) }
                }
            }
            is Resource.Success -> {
                LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                    items(result.data ?: emptyList()) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            onClick = { onMediaClick(anime.id) },
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
            is Resource.Error -> {
                Text(text = result.message ?: "Unknown error", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
