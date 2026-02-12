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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String) -> Unit
) {
    val trendingAnime by viewModel.trendingAnime.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (continueWatching is Resource.Success && continueWatching.data?.isNotEmpty() == true) {
                item {
                    HomeSection(
                        title = "Continue Watching",
                        resource = continueWatching,
                        onMediaClick = onMediaClick
                    )
                }
            }

            item {
                HomeSection(
                    title = "Trending Now",
                    resource = trendingAnime,
                    onMediaClick = onMediaClick
                )
            }

            item {
                HomeSection(
                    title = "Recommended For You",
                    resource = trendingAnime,
                    onMediaClick = onMediaClick
                )
            }
        }
    }
}

@Composable
fun HomeSection(
    title: String,
    resource: Resource<List<com.meigetsu.core.model.Anime>>,
    onMediaClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        when (resource) {
            is Resource.Loading -> {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(3) { LoadingSkeleton(Modifier.width(140.dp).padding(end = 8.dp)) }
                }
            }
            is Resource.Success -> {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(resource.data ?: emptyList()) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            rating = anime.rating,
                            type = anime.format.name,
                            status = anime.status.name,
                            onClick = { onMediaClick(anime.id) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
            is Resource.Error -> {
                Text(text = resource.message ?: "Error", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}
