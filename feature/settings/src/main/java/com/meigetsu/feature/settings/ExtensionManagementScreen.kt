package com.meigetsu.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.extensions.Extension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionManagementScreen(
    viewModel: ExtensionViewModel,
    onBackClick: () -> Unit
) {
    val extensions by viewModel.installedExtensions.collectAsState()
    val disabledIds by viewModel.disabledExtensionIds.collectAsState()
    var showAddRepoDialog by remember { mutableStateOf(false) }

    if (showAddRepoDialog) {
        AddRepoDialog(
            onDismiss = { showAddRepoDialog = false },
            onAdd = { viewModel.addExtensionRepo(it); showAddRepoDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Extensions", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddRepoDialog = true }) {
                        Icon(Icons.Rounded.AddLink, contentDescription = "Add Repo")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Text(
                    "Installed Extensions",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(extensions) { extension ->
                ExtensionItem(
                    extension = extension,
                    isEnabled = !disabledIds.contains(extension.metadata.id),
                    onToggle = { viewModel.toggleExtension(extension.metadata.id) }
                )
            }

            if (extensions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No extensions found", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun ExtensionItem(
    extension: Extension,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    ListItem(
        headlineContent = { Text(extension.metadata.name, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text("v${extension.metadata.version} • ${extension.metadata.author}", color = Color.Gray) },
        leadingContent = {
            if (extension.metadata.iconUrl != null) {
                AsyncImage(
                    model = extension.metadata.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                )
            } else {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Extension, null, tint = Color.Gray)
                    }
                }
            }
        },
        trailingContent = {
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
fun AddRepoDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Extension Repository") },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                placeholder = { Text("https://example.com/repo.json") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onAdd(url) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
