package com.meigetsu.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meigetsu.core.extensions.ExtensionManager
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    val extensionManager: ExtensionManager
) : ViewModel()

@Composable
fun ExtensionManagementScreen(
    viewModel: ExtensionViewModel
) {
    val animeProviders by viewModel.extensionManager.animeProviders.collectAsState()
    var repoUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Extensions", style = MaterialTheme.typography.headlineSmall)

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = repoUrl,
                onValueChange = { repoUrl = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Repository URL") }
            )
            IconButton(onClick = {
                scope.launch {
                    viewModel.extensionManager.fetchExtensions(repoUrl)
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Installed Extensions", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(animeProviders.values.toList()) { source ->
                ListItem(
                    headlineContent = { Text(source.metadata.name) },
                    supportingContent = { Text("Version ${source.metadata.version}") },
                    trailingContent = {
                        IconButton(onClick = { /* Remove */ }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                )
            }
        }
    }
}
