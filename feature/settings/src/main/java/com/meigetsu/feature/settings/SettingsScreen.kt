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
    val themeMode by viewModel.themeMode.collectAsState()
    val primaryColor by viewModel.primaryColor.collectAsState()
    val incognito by viewModel.incognitoMode.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val dataSaver by viewModel.dataSaverEnabled.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val defaultQuality by viewModel.defaultQuality.collectAsState()
    val autoNext by viewModel.autoNextEnabled.collectAsState()
    val skipIntro by viewModel.skipIntroAutoEnabled.collectAsState()
    val readingMode by viewModel.readingMode.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("SETTINGS", fontWeight = FontWeight.Black, letterSpacing = 2.sp) },
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
                SettingsGroup(title = "Appearance") {
                    SettingsItem(
                        icon = Icons.Rounded.Palette,
                        title = "Theme Mode",
                        description = "Current: $themeMode",
                        onClick = {
                            val next = when(themeMode) {
                                "DARK" -> "LIGHT"
                                "LIGHT" -> "SYSTEM"
                                else -> "DARK"
                            }
                            viewModel.setThemeMode(next)
                        }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.ColorLens,
                        title = "Primary Color",
                        description = "Tap to cycle colors",
                        onClick = {
                            val colors = listOf(Color(0xFFE50914), Color(0xFF00BFFF), Color(0xFF00FF00), Color(0xFFFFA500))
                            val currentIndex = colors.indexOf(primaryColor)
                            val nextColor = colors[(currentIndex + 1) % colors.size]
                            viewModel.updatePrimaryColor(nextColor)
                        }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.RoundedCorner,
                        title = "Corner Radius",
                        description = "Adjust UI roundness",
                        onClick = { viewModel.updateCornerRadius(if (viewModel.cornerRadius.value > 12) 0 else 24) }
                    )
                }
            }

            item {
                SettingsGroup(title = "Playback") {
                    SettingsItem(
                        icon = Icons.Rounded.Speed,
                        title = "Default Speed",
                        description = "Current: ${playbackSpeed}x",
                        onClick = { viewModel.setPlaybackSpeed(if (playbackSpeed >= 2.0f) 1.0f else playbackSpeed + 0.25f) }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.HighQuality,
                        title = "Default Quality",
                        description = "Current: $defaultQuality",
                        onClick = { viewModel.setDefaultQuality(if (defaultQuality == "1080p") "720p" else "1080p") }
                    )
                    SettingsToggleItem(
                        icon = Icons.Rounded.SkipNext,
                        title = "Auto Next Episode",
                        description = "Play next automatically",
                        checked = autoNext,
                        onCheckedChange = { viewModel.setAutoNext(it) }
                    )
                    SettingsToggleItem(
                        icon = Icons.Rounded.FastForward,
                        title = "Auto Skip Intro",
                        description = "Skip op/ed automatically",
                        checked = skipIntro,
                        onCheckedChange = { viewModel.setSkipIntroAuto(it) }
                    )
                }
            }

            item {
                SettingsGroup(title = "Reader") {
                    SettingsItem(
                        icon = Icons.Rounded.MenuBook,
                        title = "Reading Mode",
                        description = "Current: $readingMode",
                        onClick = { viewModel.setReadingMode(if (readingMode == "VERTICAL") "HORIZONTAL" else "VERTICAL") }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.CloudDownload,
                        title = "Preload Pages",
                        description = "Count: ${viewModel.preloadPageCount.collectAsState().value}",
                        onClick = { viewModel.setPreloadPageCount((viewModel.preloadPageCount.value % 10) + 1) }
                    )
                }
            }

            item {
                SettingsGroup(title = "Security & Data") {
                    SettingsToggleItem(
                        icon = Icons.Rounded.Fingerprint,
                        title = "Biometric Lock",
                        description = "Require auth to open app",
                        checked = biometricEnabled,
                        onCheckedChange = { viewModel.setBiometricEnabled(it) }
                    )
                    SettingsToggleItem(
                        icon = Icons.Rounded.Security,
                        title = "Incognito Mode",
                        description = "Pause history and progress",
                        checked = incognito,
                        onCheckedChange = { viewModel.setIncognito(it) }
                    )
                    SettingsToggleItem(
                        icon = Icons.Rounded.DataUsage,
                        title = "Data Saver",
                        description = "Lower quality on mobile data",
                        checked = dataSaver,
                        onCheckedChange = { viewModel.setDataSaverEnabled(it) }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.Backup,
                        title = "Backup & Restore",
                        description = "Export/Import your library",
                        onClick = { /* Logic for backup */ }
                    )
                }
            }

            item {
                SettingsGroup(title = "About") {
                    val uriHandler = LocalUriHandler.current
                    SettingsItem(
                        icon = Icons.Rounded.BarChart,
                        title = "Activity Stats",
                        description = "View your detailed history",
                        onClick = onStatsClick
                    )
                    SettingsItem(
                        icon = Icons.Rounded.Info,
                        title = "Meigetsu v1.2.0",
                        description = "Unified Anime & Manga Platform",
                        onClick = {}
                    )
                    SettingsItem(
                        icon = Icons.Rounded.Email,
                        title = "Contact Support",
                        description = "meigetsu.app@gmail.com",
                        onClick = { uriHandler.openUri("mailto:meigetsu.app@gmail.com") }
                    )
                    SettingsItem(
                        icon = Icons.Rounded.Code,
                        title = "Open Source License",
                        description = "MIT License - GitHub",
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
