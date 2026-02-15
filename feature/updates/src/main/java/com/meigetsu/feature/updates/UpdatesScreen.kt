package com.meigetsu.feature.updates

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: UpdatesViewModel
) {
    val updates by viewModel.updates.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Updates", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadUpdates() }) {
                        Icon(Icons.Rounded.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (updates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.NewReleases, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "No recent updates", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                items(updates) { update ->
                    ListItem(
                        headlineContent = { Text(update.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(update.updateInfo) },
                        leadingContent = { Icon(Icons.Rounded.History, contentDescription = null) },
                        trailingContent = { Text("Today", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
        }
    }
}
