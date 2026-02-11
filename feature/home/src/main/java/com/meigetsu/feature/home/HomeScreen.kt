package com.meigetsu.feature.home

import androidx.compose.foundation.layout.*
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Trending Now", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        when (val result = trendingAnime) {
            is Resource.Loading -> {
                LazyRow {
                    items(3) { LoadingSkeleton(Modifier.width(140.dp).padding(8.dp)) }
                }
            }
            is Resource.Success -> {
                LazyRow {
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
                Text(text = result.message ?: "Error")
            }
        }
    }
}
