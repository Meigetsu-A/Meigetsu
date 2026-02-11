package com.meigetsu.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.MediaCard
import com.meigetsu.core.ui.components.LoadingSkeleton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String) -> Unit
) {
    val trendingAnime by viewModel.trendingAnime.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            HomeSection(
                title = "Trending Now",
                resource = trendingAnime,
                onMediaClick = onMediaClick
            )
        }
        item {
            HomeSection(
                title = "Popular Anime",
                resource = trendingAnime, // Reuse for now or add new call
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
fun HomeSection(
    title: String,
    resource: Resource<List<com.meigetsu.core.model.Anime>>,
    onMediaClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        when (resource) {
            is Resource.Loading -> {
                LazyRow {
                    items(3) { LoadingSkeleton(Modifier.width(140.dp).padding(8.dp)) }
                }
            }
            is Resource.Success -> {
                LazyRow {
                    items(resource.data ?: emptyList()) { anime ->
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
                Text(text = resource.message ?: "Error", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
