package com.meigetsu.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onStatsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Black,
                    scrolledContainerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                SettingsGroup(title = "General") {
                    SettingsItem(
                        icon = Icons.Rounded.BarChart,
                        title = "Activity Stats",
                        description = "View your watch and read history",
                        onClick = onStatsClick
                    )
                    val incognito by viewModel.incognitoMode.collectAsState()
                    SettingsToggleItem(
                        icon = Icons.Rounded.Security,
                        title = "Incognito Mode",
                        description = "Pause history and progress tracking",
                        checked = incognito,
                        onCheckedChange = { viewModel.setIncognito(it) }
                    )
                }
            }

            item {
                SettingsGroup(title = "Appearance") {
                    val themeMode by viewModel.themeMode.collectAsState()
                    SettingsItem(
                        icon = Icons.Rounded.Palette,
                        title = "Theme Mode",
                        description = "Current: ${themeMode.lowercase()}",
                        onClick = { /* Change logic */ }
                    )
                }
            }

            item {
                SettingsGroup(title = "Built-in Sources") {
                    SettingsItem(
                        icon = Icons.Rounded.Source,
                        title = "AniList",
                        description = "Metadata & Discovery",
                        onClick = {}
                    )
                    SettingsItem(
                        icon = Icons.Rounded.PlayCircle,
                        title = "Consumet",
                        description = "Anime Streaming",
                        onClick = {}
                    )
                    SettingsItem(
                        icon = Icons.Rounded.MenuBook,
                        title = "MangaDex",
                        description = "Manga Content",
                        onClick = {}
                    )
                }
            }

            item {
                SettingsGroup(title = "About") {
                    val uriHandler = LocalUriHandler.current
                    SettingsItem(
                        icon = Icons.Rounded.Info,
                        title = "Meigetsu v1.1.0",
                        description = "Unified Anime & Manga Aggregator",
                        onClick = {}
                    )
                    SettingsItem(
                        icon = Icons.Rounded.Code,
                        title = "Open Source",
                        description = "Visit GitHub Repository",
                        onClick = { uriHandler.openUri("https://github.com/Azu-na/Meigetsu-") }
                    )
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        Surface(
            color = Color(0xFF141414),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(description, color = Color.Gray) },
        leadingContent = { Icon(icon, null, tint = Color.White) },
        trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(description, color = Color.Gray) },
        leadingContent = { Icon(icon, null, tint = Color.White) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
