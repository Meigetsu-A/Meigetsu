package com.meigetsu.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onManageExtensionsClick: () -> Unit
) {
    val primaryColor by viewModel.primaryColor.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    val uriHandler = LocalUriHandler.current

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Theme Builder", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Primary Color")
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                                .clickable { viewModel.updatePrimaryColor(Color.Cyan) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Corner Radius: $cornerRadius dp")
                    Slider(
                        value = cornerRadius.toFloat(),
                        onValueChange = { viewModel.updateCornerRadius(it.toInt()) },
                        valueRange = 0f..24f
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Extensions", style = MaterialTheme.typography.titleMedium)
            Button(onClick = onManageExtensionsClick, modifier = Modifier.fillMaxWidth()) {
                Text("Manage Extension Repositories")
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "About & Contact", style = MaterialTheme.typography.titleMedium)
            Column {
                ListItem(
                    headlineContent = { Text("Email") },
                    supportingContent = { Text("meigetsu.app@gmail.com") },
                    modifier = Modifier.clickable { uriHandler.openUri("mailto:meigetsu.app@gmail.com") }
                )
                ListItem(
                    headlineContent = { Text("Discord") },
                    supportingContent = { Text("Join our community") },
                    modifier = Modifier.clickable { uriHandler.openUri("https://discord.gg/JskMdb4cS") }
                )
                ListItem(
                    headlineContent = { Text("GitHub") },
                    supportingContent = { Text("Source code and issues") },
                    modifier = Modifier.clickable { uriHandler.openUri("https://github.com/Azu-na/Meigetsu-") }
                )
            }
        }
    }
}
