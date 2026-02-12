package com.meigetsu.feature.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton

@Composable
fun MediaDetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    onWatchClick: (String) -> Unit,
    onReadClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val streamUrls by viewModel.streamUrls.collectAsState()

    LaunchedEffect(streamUrls) {
        if (streamUrls.isNotEmpty()) {
            onWatchClick(streamUrls.first().url)
        }
    }

    when (val state = uiState) {
        is Resource.Loading -> LoadingSkeleton()
        is Resource.Success -> {
            val anime = state.data!!
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Text(text = anime.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = anime.description ?: "No description available", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Button(onClick = {
                            if (anime.format.name == "MANGA") onReadClick(anime.id)
                            else viewModel.fetchStreams()
                        }) {
                            Text(if (anime.format.name == "MANGA") "Read Now" else "Watch Now")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { viewModel.addToLibrary() }) {
                            Text("Add to Library")
                        }
                    }
                }
            }
        }
        is Resource.Error -> {
            Text(text = state.message ?: "Error loading details")
        }
    }
}
