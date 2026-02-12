package com.meigetsu.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
    onManageExtensionsClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    val primaryColor by viewModel.primaryColor.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    val incognito by viewModel.incognitoMode.collectAsState()
    val adultContent by viewModel.adultContent.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val watchTime by viewModel.watchTime.collectAsState()
    val readCount by viewModel.readCount.collectAsState()
    val uriHandler = LocalUriHandler.current

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Statistics Section
        item {
            SettingsCategory(title = "Statistics", icon = Icons.Rounded.BarChart) {
                ListItem(
                    headlineContent = { Text("View Detailed Stats") },
                    supportingContent = { Text("Watch time and reading activity") },
                    modifier = Modifier.clickable { onStatsClick() },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null) }
                )
            }
        }

        // Appearance Section
        item {
            SettingsCategory(title = "Appearance", icon = Icons.Rounded.Palette) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Primary Color")
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                                .clickable { viewModel.updatePrimaryColor(Color.Cyan) }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Corner Radius: $cornerRadius dp", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = cornerRadius.toFloat(),
                        onValueChange = { viewModel.updateCornerRadius(it.toInt()) },
                        valueRange = 0f..24f
                    )
                }
            }
        }

        // Behavior Section
        item {
            SettingsCategory(title = "Behavior", icon = Icons.Rounded.SettingsSuggest) {
                Column {
                    ListItem(
                        headlineContent = { Text("Incognito Mode") },
                        supportingContent = { Text("Don't save history or progress") },
                        trailingContent = { Switch(checked = incognito, onCheckedChange = { viewModel.setIncognito(it) }) }
                    )
                    ListItem(
                        headlineContent = { Text("Show Adult Content") },
                        supportingContent = { Text("Include NSFW sources") },
                        trailingContent = { Switch(checked = adultContent, onCheckedChange = { viewModel.setAdultContent(it) }) }
                    )
                    ListItem(
                        headlineContent = { Text("Biometric Lock") },
                        supportingContent = { Text("Secure app with fingerprint/face") },
                        trailingContent = { Switch(checked = biometricEnabled, onCheckedChange = { viewModel.setBiometricEnabled(it) }) }
                    )
                }
            }
        }

        // Extensions Section
        item {
            SettingsCategory(title = "Extensions", icon = Icons.Rounded.Extension) {
                ListItem(
                    headlineContent = { Text("Manage Extensions") },
                    supportingContent = { Text("Install or update providers") },
                    modifier = Modifier.clickable { onManageExtensionsClick() },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null) }
                )
            }
        }

        // Backup Section
        item {
            SettingsCategory(title = "Data", icon = Icons.Rounded.Storage) {
                Column {
                    ListItem(
                        headlineContent = { Text("Backup") },
                        supportingContent = { Text("Export library and settings") },
                        modifier = Modifier.clickable { /* Backup logic */ }
                    )
                    ListItem(
                        headlineContent = { Text("Restore") },
                        supportingContent = { Text("Import from backup file") },
                        modifier = Modifier.clickable { /* Restore logic */ }
                    )
                    ListItem(
                        headlineContent = { Text("Clear Cache") },
                        modifier = Modifier.clickable { /* Clear cache logic */ }
                    )
                    ListItem(
                        headlineContent = { Text("Cloud Sync (WebDAV)") },
                        supportingContent = { Text("Sync library via custom server") },
                        modifier = Modifier.clickable { /* WebDAV Setup */ },
                        trailingContent = { Icon(Icons.Rounded.CloudSync, contentDescription = null) }
                    )
                }
            }
        }

        // About & Contact Section
        item {
            SettingsCategory(title = "About Meigetsu", icon = Icons.Rounded.Info) {
                Column {
                    ListItem(
                        headlineContent = { Text("Version") },
                        supportingContent = { Text("1.0.0-stable") }
                    )
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
}

@Composable
fun SettingsCategory(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            content()
        }
    }
}
