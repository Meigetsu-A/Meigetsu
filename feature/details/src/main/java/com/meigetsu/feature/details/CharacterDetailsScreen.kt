package com.meigetsu.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton
import com.meigetsu.core.ui.components.MediaCard
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    viewModel: CharacterDetailsViewModel,
    onBackClick: () -> Unit,
    onMediaClick: (String, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Character", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is Resource.Loading -> LoadingSkeleton(Modifier.padding(innerPadding))
            is Resource.Success -> {
                val char = state.data!!
                LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
                    item {
                        AsyncImage(
                            model = char.image,
                            contentDescription = char.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = char.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = char.description ?: "No description", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    if (char.associatedMedia.isNotEmpty()) {
                        item {
                            Text(
                                text = "Appears In",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            LazyRow(contentPadding = PaddingValues(bottom = 24.dp)) {
                                items(char.associatedMedia) { anime ->
                                    MediaCard(
                                        title = anime.title,
                                        imageUrl = anime.coverImage,
                                        modifier = Modifier.width(140.dp).padding(end = 12.dp),
                                        onClick = { onMediaClick(anime.id, "ANIME") }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message ?: "Error", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
