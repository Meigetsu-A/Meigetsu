package com.meigetsu.feature.updates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpdatesScreen(
    viewModel: UpdatesViewModel
) {
    val updates by viewModel.updates.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Recently Updated", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (updates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "No updates yet", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
        } else {
            LazyColumn {
                items(updates) { update ->
                    ListItem(
                        headlineContent = { Text(update.title) },
                        supportingContent = { Text(update.updateInfo) }
                    )
                }
            }
        }
    }
}
