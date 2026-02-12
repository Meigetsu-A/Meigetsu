package com.meigetsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.meigetsu.core.ui.theme.MeigetsuTheme
import com.meigetsu.feature.browse.BrowseScreen
import com.meigetsu.feature.details.MediaDetailsScreen
import com.meigetsu.feature.details.CharacterDetailsScreen
import com.meigetsu.feature.home.HomeScreen
import com.meigetsu.feature.library.LibraryScreen
import com.meigetsu.feature.settings.SettingsScreen
import com.meigetsu.feature.settings.ExtensionManagementScreen
import com.meigetsu.feature.updates.UpdatesScreen
import com.meigetsu.feature.player.PlayerScreen
import com.meigetsu.feature.reader.ReaderScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val primaryColor by viewModel.primaryColor.collectAsState()
            val cornerRadius by viewModel.cornerRadius.collectAsState()

            MeigetsuTheme(primaryColor = primaryColor, cornerRadius = cornerRadius) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = items.any { it.route == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                HomeScreen(hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Library.route) {
                LibraryScreen(hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Updates.route) {
                UpdatesScreen(hiltViewModel())
            }
            composable(Screen.Browse.route) {
                BrowseScreen(hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(hiltViewModel(), onManageExtensionsClick = { navController.navigate("extensions") })
            }
            composable("extensions") {
                ExtensionManagementScreen(hiltViewModel())
            }
            composable(
                "details/{mediaId}",
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) {
                MediaDetailsScreen(
                    hiltViewModel(),
                    onBackClick = { navController.popBackStack() },
                    onWatchClick = { url ->
                        val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                        navController.navigate("player/$encodedUrl")
                    },
                    onReadClick = { id -> navController.navigate("reader/$id") }
                )
            }
            composable(
                "character/{charId}",
                arguments = listOf(navArgument("charId") { type = NavType.StringType })
            ) {
                CharacterDetailsScreen(hiltViewModel())
            }
            composable(
                "player/{url}",
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) {
                PlayerScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
            }
            composable(
                "reader/{chapterId}",
                arguments = listOf(navArgument("chapterId") { type = NavType.StringType })
            ) {
                ReaderScreen(hiltViewModel())
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Library : Screen("library", "Library", Icons.Default.List)
    object Updates : Screen("updates", "Updates", Icons.Default.Refresh)
    object Browse : Screen("browse", "Browse", Icons.Default.Search)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Updates,
    Screen.Browse,
    Screen.Settings
)
