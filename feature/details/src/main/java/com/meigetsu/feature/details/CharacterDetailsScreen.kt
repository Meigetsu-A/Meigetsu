package com.meigetsu.feature.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton
import coil.compose.AsyncImage

@Composable
fun CharacterDetailsScreen(
    viewModel: CharacterDetailsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is Resource.Loading -> LoadingSkeleton()
        is Resource.Success -> {
            val char = state.data!!
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    AsyncImage(
                        model = char.image,
                        contentDescription = char.name,
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = char.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = char.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        is Resource.Error -> {
            Text(text = state.message ?: "Error")
        }
    }
}
