package com.meigetsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.meigetsu.core.ui.theme.MeigetsuTheme
import com.meigetsu.feature.browse.BrowseScreen
import com.meigetsu.feature.details.MediaDetailsScreen
import com.meigetsu.feature.home.HomeScreen
import com.meigetsu.feature.library.LibraryScreen
import com.meigetsu.feature.settings.SettingsScreen
import com.meigetsu.feature.updates.UpdatesScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeigetsuTheme {
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
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
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
                SettingsScreen(hiltViewModel())
            }
            composable("details/{mediaId}") {
                MediaDetailsScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Home", androidx.compose.material.icons.Icons.Filled.Home)
    object Library : Screen("library", "Library", androidx.compose.material.icons.Icons.Filled.List)
    object Updates : Screen("updates", "Updates", androidx.compose.material.icons.Icons.Filled.Refresh)
    object Browse : Screen("browse", "Browse", androidx.compose.material.icons.Icons.Filled.Search)
    object Settings : Screen("settings", "Settings", androidx.compose.material.icons.Icons.Filled.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Updates,
    Screen.Browse,
    Screen.Settings
)
