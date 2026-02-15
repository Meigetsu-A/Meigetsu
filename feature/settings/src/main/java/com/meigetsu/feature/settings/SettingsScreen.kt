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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onManageExtensionsClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("General", "Appearance", "Extensions", "Security", "About")

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(title = { Text("Settings", fontWeight = FontWeight.Bold) })
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.background,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> GeneralSettings(viewModel, onStatsClick)
                1 -> AppearanceSettings(viewModel)
                2 -> ExtensionSettings(viewModel, onManageExtensionsClick)
                3 -> SecuritySettings(viewModel)
                4 -> AboutSettings()
            }
        }
    }
}

@Composable
fun GeneralSettings(viewModel: SettingsViewModel, onStatsClick: () -> Unit) {
    val incognito by viewModel.incognitoMode.collectAsState()
    val autoRefresh by viewModel.autoRefreshInterval.collectAsState()
    val libraryLayout by viewModel.libraryLayout.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SettingsCategory(title = "Library", icon = Icons.Rounded.LibraryBooks) {
                ListItem(
                    headlineContent = { Text("Library Layout") },
                    supportingContent = { Text("Current: $libraryLayout") },
                    modifier = Modifier.clickable {
                        viewModel.setLibraryLayout(if (libraryLayout == "GRID") "LIST" else "GRID")
                    },
                    trailingContent = { Icon(if (libraryLayout == "GRID") Icons.Rounded.GridView else Icons.Rounded.List, null) }
                )
                ListItem(
                    headlineContent = { Text("Incognito Mode") },
                    supportingContent = { Text("Don't save history or progress") },
                    trailingContent = { Switch(checked = incognito, onCheckedChange = { viewModel.setIncognito(it) }) }
                )
            }
        }
        item {
            SettingsCategory(title = "Updates", icon = Icons.Rounded.Update) {
                ListItem(
                    headlineContent = { Text("Auto Refresh Interval") },
                    supportingContent = { Text(if (autoRefresh == 0) "Disabled" else "$autoRefresh minutes") },
                    modifier = Modifier.clickable {
                        // Simplified selection
                        viewModel.setAutoRefreshInterval(if (autoRefresh == 0) 60 else 0)
                    },
                    trailingContent = { Icon(Icons.Rounded.Timer, null) }
                )
            }
        }
        item {
            SettingsCategory(title = "Statistics", icon = Icons.Rounded.BarChart) {
                ListItem(
                    headlineContent = { Text("Activity Log") },
                    supportingContent = { Text("View detailed watch/read history") },
                    modifier = Modifier.clickable { onStatsClick() },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) }
                )
            }
        }
        item {
            SettingsCategory(title = "Data", icon = Icons.Rounded.Storage) {
                ListItem(
                    headlineContent = { Text("Backup & Restore") },
                    modifier = Modifier.clickable { }
                )
                ListItem(
                    headlineContent = { Text("Clear Cache") },
                    modifier = Modifier.clickable { }
                )
            }
        }
    }
}

@Composable
fun AppearanceSettings(viewModel: SettingsViewModel) {
    val primaryColor by viewModel.primaryColor.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val cornerRadius by viewModel.cornerRadius.collectAsState()
    var showThemeMaker by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SettingsCategory(title = "Theme Mode", icon = Icons.Rounded.Contrast) {
                Column {
                    listOf("SYSTEM", "LIGHT", "DARK", "OLED").forEach { mode ->
                        ListItem(
                            headlineContent = { Text(mode.lowercase().replaceFirstChar { it.uppercase() }) },
                            trailingContent = { RadioButton(selected = themeMode == mode, onClick = { viewModel.setThemeMode(mode) }) },
                            modifier = Modifier.clickable { viewModel.setThemeMode(mode) }
                        )
                    }
                }
            }
        }
        item {
            SettingsCategory(title = "Customization", icon = Icons.Rounded.Palette) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Theme Maker")
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = { showThemeMaker = true }) {
                            Text("Open")
                        }
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
    }

    if (showThemeMaker) {
        ThemeMakerDialog(
            currentColor = primaryColor,
            onColorChange = { viewModel.updatePrimaryColor(it) },
            onDismiss = { showThemeMaker = false }
        )
    }
}

@Composable
fun ThemeMakerDialog(
    currentColor: Color,
    onColorChange: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        Color(0xFFE50914), Color(0xFF2196F3), Color(0xFF4CAF50),
        Color(0xFFFFC107), Color(0xFF9C27B0), Color(0xFF00BCD4),
        Color(0xFFFF5722), Color(0xFF607D8B), Color(0xFFE91E63)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Theme Maker") },
        text = {
            Column {
                Text("Select Primary Color", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(16.dp))
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(colors.size) { index ->
                        val color = colors[index]
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { onColorChange(color) }
                                .let {
                                    if (color == currentColor) it.background(color, CircleShape).padding(4.dp).background(Color.White, CircleShape)
                                    else it
                                }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
fun ExtensionSettings(viewModel: SettingsViewModel, onManageExtensionsClick: () -> Unit) {
    val repositories by viewModel.repositories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newRepoUrl by remember { mutableStateOf("") }
    var newRepoName by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SettingsCategory(title = "Management", icon = Icons.Rounded.Extension) {
                ListItem(
                    headlineContent = { Text("Browse Extensions") },
                    supportingContent = { Text("Install and update plugins") },
                    modifier = Modifier.clickable { onManageExtensionsClick() },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null) }
                )
            }
        }
        item {
            SettingsCategory(title = "Repositories", icon = Icons.Rounded.Source) {
                Column {
                    repositories.forEach { repo ->
                        ListItem(
                            headlineContent = { Text(repo.name) },
                            supportingContent = { Text(repo.url) },
                            trailingContent = {
                                IconButton(onClick = { viewModel.removeRepository(repo.url) }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = "Delete")
                                }
                            }
                        )
                    }
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Icon(Icons.Rounded.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add Repository")
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Repository") },
            text = {
                Column {
                    TextField(
                        value = newRepoName,
                        onValueChange = { newRepoName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = newRepoUrl,
                        onValueChange = { newRepoUrl = it },
                        label = { Text("URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addRepository(newRepoUrl, newRepoName)
                    showAddDialog = false
                    newRepoUrl = ""
                    newRepoName = ""
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SecuritySettings(viewModel: SettingsViewModel) {
    val adultContent by viewModel.adultContent.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SettingsCategory(title = "Privacy", icon = Icons.Rounded.Security) {
                ListItem(
                    headlineContent = { Text("Show Adult Content") },
                    trailingContent = { Switch(checked = adultContent, onCheckedChange = { viewModel.setAdultContent(it) }) }
                )
                ListItem(
                    headlineContent = { Text("Biometric Lock") },
                    trailingContent = { Switch(checked = biometricEnabled, onCheckedChange = { viewModel.setBiometricEnabled(it) }) }
                )
            }
        }
    }
}

@Composable
fun AboutSettings() {
    val uriHandler = LocalUriHandler.current
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            SettingsCategory(title = "Meigetsu", icon = Icons.Rounded.Info) {
                ListItem(headlineContent = { Text("Version") }, supportingContent = { Text("1.0.0-stable") })
                ListItem(
                    headlineContent = { Text("GitHub") },
                    modifier = Modifier.clickable { uriHandler.openUri("https://github.com/Azu-na/Meigetsu-") }
                )
                ListItem(
                    headlineContent = { Text("Discord") },
                    modifier = Modifier.clickable { uriHandler.openUri("https://discord.gg/JskMdb4cS") }
                )
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
