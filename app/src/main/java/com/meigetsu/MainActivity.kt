package com.meigetsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.meigetsu.core.ui.theme.*
import com.meigetsu.feature.home.HomeScreen
import com.meigetsu.feature.search.SearchScreen
import com.meigetsu.feature.browse.BrowseScreen
import com.meigetsu.feature.library.LibraryScreen
import com.meigetsu.feature.profile.ProfileScreen
import com.meigetsu.feature.details.MediaDetailScreen
import com.meigetsu.feature.player.PlayerScreen
import com.meigetsu.feature.reader.ReaderScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val primaryColor by viewModel.primaryColor.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()

            MeigetsuTheme(
                primaryColor = primaryColor,
                isDarkTheme = themeMode == "DARK" || themeMode == "SYSTEM"
            ) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(currentDestination?.route)) {
                CustomBottomNavigation(navController)
            }
        },
        containerColor = Background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(hiltViewModel(), onMediaClick = { id, type -> navController.navigate("detail/$id/$type") })
            }
            composable("search") {
                SearchScreen(hiltViewModel(), onMediaClick = { id, type -> navController.navigate("detail/$id/$type") })
            }
            composable("browse") {
                BrowseScreen(hiltViewModel(), onMediaClick = { id, type -> navController.navigate("detail/$id/$type") })
            }
            composable("library") {
                LibraryScreen(hiltViewModel(), onMediaClick = { id, type -> navController.navigate("detail/$id/$type") })
            }
            composable("profile") {
                ProfileScreen(hiltViewModel())
            }

            composable("detail/{id}/{type}") {
                MediaDetailScreen(
                    hiltViewModel(),
                    onBackClick = { navController.popBackStack() },
                    onWatchClick = { id, source -> navController.navigate("player/$id/$source") },
                    onReadClick = { id, source -> navController.navigate("reader/$id/$source") }
                )
            }
            composable("player/{id}/{source}") {
                PlayerScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
            }
            composable("reader/{id}/{source}") {
                ReaderScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
            }
        }
    }
}

fun shouldShowBottomBar(route: String?): Boolean {
    return route in listOf("home", "search", "browse", "library", "profile")
}

@Composable
fun CustomBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        NavigationItem("home", "HOME") { HomeIcon(it) },
        NavigationItem("search", "SEARCH") { SearchIcon(it) },
        NavigationItem("browse", "BROWSE") { BrowseIcon(it) },
        NavigationItem("library", "LIBRARY") { LibraryIcon(it) },
        NavigationItem("profile", "PROFILE") { ProfileIcon(it) }
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Background)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(2.dp)
                            .background(PrimaryText)
                    )
                    Spacer(Modifier.height(8.dp))
                } else {
                    Spacer(Modifier.height(10.dp))
                }

                item.icon(selected)

                Text(
                    text = item.label,
                    style = Typography.labelSmall,
                    color = if (selected) PrimaryText else MutedText,
                    fontSize = 10.sp
                )
            }
        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: @Composable (Boolean) -> Unit)

@Composable fun HomeIcon(selected: Boolean) {
    Icon(
        imageVector = if (selected) Icons.Rounded.Home else Icons.Rounded.Home,
        contentDescription = null,
        tint = if (selected) PrimaryText else MutedText,
        modifier = Modifier.size(24.dp)
    )
}

@Composable fun SearchIcon(selected: Boolean) {
    Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = null,
        tint = if (selected) PrimaryText else MutedText,
        modifier = Modifier.size(24.dp)
    )
}

@Composable fun BrowseIcon(selected: Boolean) {
    Icon(
        imageVector = Icons.Rounded.GridView,
        contentDescription = null,
        tint = if (selected) PrimaryText else MutedText,
        modifier = Modifier.size(24.dp)
    )
}

@Composable fun LibraryIcon(selected: Boolean) {
    Icon(
        imageVector = Icons.Rounded.CollectionsBookmark,
        contentDescription = null,
        tint = if (selected) PrimaryText else MutedText,
        modifier = Modifier.size(24.dp)
    )
}

@Composable fun ProfileIcon(selected: Boolean) {
    Icon(
        imageVector = Icons.Rounded.Person,
        contentDescription = null,
        tint = if (selected) PrimaryText else MutedText,
        modifier = Modifier.size(24.dp)
    )
}
