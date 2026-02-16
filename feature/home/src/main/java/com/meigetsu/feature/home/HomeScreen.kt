package com.meigetsu.feature.home

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.*
import com.meigetsu.core.model.Anime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String) -> Unit
) {
    val trendingAnime by viewModel.trendingAnime.collectAsState()
    val popularAnime by viewModel.popularAnime.collectAsState()
    val recommendedAnime by viewModel.recommendedAnime.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "MEIGETSU",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Rounded.Search, null)
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Rounded.NotificationsNone, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Black.copy(alpha = 0.8f)
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Spotlight Section
            item {
                SpotlightSection(trendingAnime, onMediaClick)
            }

            item { Spacer(Modifier.height(24.dp)) }

            // Continue Watching
            if (continueWatching is Resource.Success && continueWatching.data?.isNotEmpty() == true) {
                item {
                    ModernHomeSection("Continue Playing", continueWatching, onMediaClick)
                }
            }

            // Popular Section
            item {
                ModernHomeSection("Must Watch Anime", popularAnime, onMediaClick)
            }

            // Recommended Section
            item {
                ModernHomeSection("For You", recommendedAnime, onMediaClick)
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotlightSection(
    resource: Resource<List<Anime>>,
    onMediaClick: (String) -> Unit
) {
    val items = (resource as? Resource.Success)?.data?.take(5) ?: emptyList()
    val pagerState = rememberPagerState { items.size }

    Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
        if (items.isNotEmpty()) {
            HorizontalPager(state = pagerState) { index ->
                val anime = items[index]
                Box(modifier = Modifier.fillMaxSize().clickable { onMediaClick(anime.id) }) {
                    AsyncImage(
                        model = anime.bannerImage ?: anime.coverImage,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Glassy Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                "FEATURED",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = anime.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = anime.genres.take(3).joinToString(" • "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Pager Indicator
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(items.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.Gray
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(color)
                    )
                }
            }
        } else {
            LoadingSkeleton(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun ModernHomeSection(
    title: String,
    resource: Resource<List<Anime>>,
    onMediaClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "See all",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (resource) {
            is Resource.Loading -> {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp)) {
                    items(5) { LoadingSkeleton(Modifier.width(140.dp).height(200.dp).padding(end = 12.dp)) }
                }
            }
            is Resource.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resource.data ?: emptyList()) { anime ->
                        MediaCard(
                            title = anime.title,
                            imageUrl = anime.coverImage,
                            rating = anime.rating,
                            status = if (anime.status == com.meigetsu.core.model.MediaStatus.RELEASING) "LIVE" else null,
                            onClick = { onMediaClick(anime.id) }
                        )
                    }
                }
            }
            is Resource.Error -> {
                // Simplified error view
            }
        }
    }
}
