package com.meigetsu.feature.player

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.model.Episode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeBottomSheet(
    episodes: List<Episode>,
    onEpisodeClick: (Episode) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            items(episodes) { episode ->
                ListItem(
                    headlineContent = { Text("Episode ${episode.number}") },
                    supportingContent = { Text(episode.title ?: "") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        }
    }
}
