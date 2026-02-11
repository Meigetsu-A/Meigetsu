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
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    Button(onClick = { /* Add to library */ }) {
                        Text("Add to Library")
                    }
                }
                // Episodes list could go here
            }
        }
        is Resource.Error -> {
            Text(text = state.message ?: "Error loading details")
        }
    }
}
