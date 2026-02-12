package com.meigetsu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.meigetsu.feature.settings.StatsScreen
import com.meigetsu.feature.updates.UpdatesScreen
import com.meigetsu.feature.news.NewsScreen
import com.meigetsu.feature.player.PlayerScreen
import com.meigetsu.feature.reader.ReaderScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : androidx.fragment.app.FragmentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val primaryColor by viewModel.primaryColor.collectAsState()
            val cornerRadius by viewModel.cornerRadius.collectAsState()
            val biometricEnabled by viewModel.biometricEnabled.collectAsState()
            var isAuthenticated by remember { mutableStateOf(!biometricEnabled) }

            if (biometricEnabled && !isAuthenticated) {
                LaunchedEffect(Unit) {
                    showBiometricPrompt { authenticated ->
                        isAuthenticated = authenticated
                    }
                }
            }

            MeigetsuTheme(primaryColor = primaryColor, cornerRadius = cornerRadius) {
                if (isAuthenticated) {
                    MainScreen()
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Button(onClick = {
                            showBiometricPrompt { authenticated -> isAuthenticated = authenticated }
                        }) {
                            Text("Unlock Meigetsu")
                        }
                    }
                }
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        // Simple logic to enter PIP if we are on player screen
        // In a real app, we check if video is playing
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            enterPictureInPictureMode(android.app.PictureInPictureParams.Builder().build())
        }
    }

    private fun showBiometricPrompt(onResult: (Boolean) -> Unit) {
        val executor = androidx.core.content.ContextCompat.getMainExecutor(this)
        val biometricPrompt = androidx.biometric.BiometricPrompt(this, executor,
            object : androidx.biometric.BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: androidx.biometric.BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onResult(true)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onResult(false)
                }
            })

        val promptInfo = androidx.biometric.BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for Meigetsu")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = items.any { it.route == currentDestination?.route }

            if (showBottomBar) {
                NavigationBar(tonalElevation = 8.dp) {
                    items.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(screen.label) },
                            selected = selected,
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
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            Modifier.padding(innerPadding),
            enterTransition = { fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            exitTransition = { fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start) },
            popEnterTransition = { fadeIn() + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End) },
            popExitTransition = { fadeOut() + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Library.route) {
                LibraryScreen(hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Updates.route) {
                UpdatesScreen(hiltViewModel())
            }
            composable(Screen.News.route) {
                NewsScreen(hiltViewModel())
            }
            composable(Screen.Browse.route) {
                BrowseScreen(hiltViewModel(), hiltViewModel(), onMediaClick = { id -> navController.navigate("details/$id") })
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    hiltViewModel(),
                    onManageExtensionsClick = { navController.navigate("extensions") },
                    onStatsClick = { navController.navigate("stats") }
                )
            }
            composable("extensions") {
                ExtensionManagementScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
            }
            composable("stats") {
                StatsScreen(hiltViewModel(), onBackClick = { navController.popBackStack() })
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

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : Screen("home", "Home", Icons.Rounded.Home, Icons.Rounded.Home)
    object Library : Screen("library", "Library", Icons.Rounded.AutoStories, Icons.Rounded.AutoStories)
    object Updates : Screen("updates", "Updates", Icons.Rounded.Update, Icons.Rounded.Update)
    object News : Screen("news", "News", Icons.Rounded.Newspaper, Icons.Rounded.Newspaper)
    object Browse : Screen("browse", "Browse", Icons.Rounded.Explore, Icons.Rounded.Explore)
    object Settings : Screen("settings", "Settings", Icons.Rounded.Settings, Icons.Rounded.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.Library,
    Screen.Updates,
    Screen.News,
    Screen.Browse,
    Screen.Settings
)
