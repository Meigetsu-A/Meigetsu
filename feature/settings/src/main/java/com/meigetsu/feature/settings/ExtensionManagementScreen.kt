package com.meigetsu.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.AnimeProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionManagementScreen(
    viewModel: ExtensionViewModel,
    onBackClick: () -> Unit
) {
    val animeProviders by viewModel.extensionManager.animeProviders.collectAsState()
    val availableExtensions by viewModel.extensionManager.availableExtensions.collectAsState()
    val isScanning by viewModel.extensionManager.isScanning.collectAsState()
    val repos by viewModel.repos.collectAsState()
    var repoUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Extensions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.scanExtensions() }) {
                        if (isScanning) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Scan for extensions")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                value = repoUrl,
                onValueChange = { repoUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Repository URL") },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.addRepo(repoUrl)
                        repoUrl = ""
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Repositories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            repos.forEach { repo ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = repo.url, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Checkbox(checked = repo.isTrusted, onCheckedChange = { viewModel.toggleTrust(repo) })
                    IconButton(onClick = { viewModel.deleteRepo(repo) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Installed Extensions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(animeProviders.values.toList()) { source ->
                    ElevatedCard {
                        ListItem(
                            headlineContent = { Text(source.metadata.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("v${source.metadata.version} • ${source.metadata.author} ${if (source.metadata.isTrusted) "✓" else "⚠"}") },
                            leadingContent = { Icon(Icons.Rounded.Extension, contentDescription = null) },
                            trailingContent = {
                                IconButton(onClick = { /* Remove */ }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Available Extensions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(availableExtensions) { extension ->
                    ElevatedCard {
                        ListItem(
                            headlineContent = { Text(extension.name) },
                            supportingContent = { Text("${extension.pkg} • v${extension.version}") },
                            trailingContent = {
                                Button(onClick = { scope.launch { viewModel.extensionManager.installExtension(extension) } }) {
                                    Text("Install")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
