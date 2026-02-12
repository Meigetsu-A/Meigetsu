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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String) -> Unit
) {
    val trendingAnime by viewModel.trendingAnime.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Meigetsu", fontWeight = androidx.compose.ui.text.font.FontWeight.Black) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                    title = "Popular on Meigetsu",
                    resource = trendingAnime, // Simulated
                    onMediaClick = onMediaClick
                )
            }

            item {
                HomeSection(
                    title = "Recommended For You",
                    resource = trendingAnime, // Simulated
                    onMediaClick = onMediaClick
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun HomeSection(
    title: String,
    resource: Resource<List<com.meigetsu.core.model.Anime>>,
    onMediaClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 16.dp),
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (resource) {
            is Resource.Loading -> {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(5) { LoadingSkeleton(Modifier.width(160.dp).padding(end = 12.dp)) }
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
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            }
            is Resource.Error -> {
                Text(
                    text = resource.message ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
