package com.meigetsu

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pages = listOf(
        OnboardingPage.Welcome,
        OnboardingPage.Aggregator,
        OnboardingPage.Permissions,
        OnboardingPage.Legal
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { index ->
            OnboardingContent(pages[index]) {
                if (index < pages.size - 1) {
                    scope.launch { pagerState.animateScrollToPage(index + 1) }
                } else {
                    onComplete()
                }
            }
        }

        // Indicator
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(color, MaterialTheme.shapes.extraLarge)
                        .size(12.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingContent(
    page: OnboardingPage,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // We proceed anyway for now, or we could check if all are granted
        onNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (page is OnboardingPage.Permissions) {
                    val permissions = mutableListOf<String>()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    // For files, usually we don't need broad storage permission on modern Android unless scanning whole storage
                    // But for "access downloaded extensions", if they are in public folders:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                    if (permissions.isNotEmpty()) {
                        permissionLauncher.launch(permissions.toTypedArray())
                    } else {
                        onNext()
                    }
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(page.buttonText)
        }
    }
}

sealed class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val buttonText: String
) {
    object Welcome : OnboardingPage(
        "Welcome to Meigetsu",
        "Your new home for Anime and Manga content aggregation. Elegant, fast, and powerful.",
        Icons.Rounded.AutoAwesome,
        "Get Started"
    )
    object Aggregator : OnboardingPage(
        "Content Aggregator",
        "Meigetsu does not host any content. It provides a browser-like interface to access third-party extensions.",
        Icons.Rounded.TravelExplore,
        "I Understand"
    )
    object Permissions : OnboardingPage(
        "Permissions",
        "To provide the best experience, we need permissions to show notifications and access local extension files.",
        Icons.Rounded.Security,
        "Grant Permissions"
    )
    object Legal : OnboardingPage(
        "Legal Disclaimer",
        "By using this app, you agree to our Terms of Service and acknowledge that you are responsible for the extensions you install.",
        Icons.Rounded.Gavel,
        "Agree & Finish"
    )
}
