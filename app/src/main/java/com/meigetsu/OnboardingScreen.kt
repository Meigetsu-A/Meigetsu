package com.meigetsu

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pages = listOf(
        OnboardingPage.Welcome,
        OnboardingPage.Unified,
        OnboardingPage.Legal
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
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

        // Elegant Indicator
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pages.size) { iteration ->
                val width by animateDpAsState(if (pagerState.currentPage == iteration) 24.dp else 8.dp)
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.DarkGray
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(width)
                        .background(color, androidx.compose.foundation.shape.CircleShape)
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
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { onNext() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(160.dp),
            color = MaterialTheme.colorScheme.primary.copy(0.1f),
            shape = androidx.compose.foundation.shape.CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                if (page == OnboardingPage.Welcome) {
                    val permissions = mutableListOf<String>()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    if (permissions.isNotEmpty()) permissionLauncher.launch(permissions.toTypedArray())
                    else onNext()
                } else {
                    onNext()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(page.buttonText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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
        "MEIGETSU",
        "Experience Anime and Manga like never before. Minimalist, fast, and unified.",
        Icons.Rounded.AutoAwesome,
        "Get Started"
    )
    object Unified : OnboardingPage(
        "Anime & Manga",
        "Switch between watching your favorite series and reading the latest chapters seamlessly in one place.",
        Icons.Rounded.AutoStories,
        "Next"
    )
    object Legal : OnboardingPage(
        "Safe & Secure",
        "We prioritize your privacy and respect content creators. Meigetsu is a tool for accessing public content.",
        Icons.Rounded.Gavel,
        "Agree & Enter"
    )
}
