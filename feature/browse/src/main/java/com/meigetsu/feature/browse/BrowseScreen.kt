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

    Column {
        TextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(it)
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search Anime...") }
        )

        when (val result = searchResult) {
            is Resource.Loading -> {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(6) { LoadingSkeleton(Modifier.padding(8.dp)) }
                }
            }
            is Resource.Success -> {
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(result.data ?: emptyList()) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            onClick = { onMediaClick(anime.id) },
                            modifier = Modifier.padding(8.dp)
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
