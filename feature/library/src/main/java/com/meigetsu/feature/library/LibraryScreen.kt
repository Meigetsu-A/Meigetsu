package com.meigetsu.feature.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.ui.components.MediaCard

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onMediaClick: (String) -> Unit
) {
    val libraryAnime by viewModel.libraryAnime.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Library", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (libraryAnime.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Your library is empty", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(libraryAnime) { anime ->
                    MediaCard(
                        title = anime.title,
                        imageUrl = anime.coverImage,
                        onClick = { onMediaClick(anime.id) },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
