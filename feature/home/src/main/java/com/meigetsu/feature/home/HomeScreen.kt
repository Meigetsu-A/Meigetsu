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
    onMediaClick: (String, String) -> Unit
) {
    val trendingAnime by viewModel.trendingAnime.collectAsState()
    val popularAnime by viewModel.popularAnime.collectAsState()
    val trendingManga by viewModel.trendingManga.collectAsState()
    val popularManga by viewModel.popularManga.collectAsState()
    val recommendedAnime by viewModel.recommendedAnime.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val topSources by viewModel.topSources.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchResultsAnime by viewModel.searchResultsAnime.collectAsState()
    val searchResultsManga by viewModel.searchResultsManga.collectAsState()

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
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
                        IconButton(onClick = { /* Notifications */ }) {
                            Icon(Icons.Rounded.NotificationsNone, null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Black.copy(alpha = 0.8f)
                    )
                )

                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.updateSearchQuery(it) },
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("Search Anime & Manga...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = SearchBarDefaults.colors(containerColor = Color(0xFF141414))
                ) { }

                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.genres) { genre ->
                        FilterChip(
                            selected = selectedGenre == genre,
                            onClick = { viewModel.updateGenre(genre) },
                            label = { Text(genre) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        if (isSearching || searchQuery.isNotBlank() || selectedGenre != null) {
            SearchResultsContent(
                searchResultsAnime,
                searchResultsManga,
                innerPadding,
                onMediaClick
            )
        } else {
            HomeContent(
                trendingAnime,
                popularAnime,
                trendingManga,
                popularManga,
                recommendedAnime,
                continueWatching,
                topSources,
                innerPadding,
                onMediaClick
            )
        }
    }
}

@Composable
fun SearchResultsContent(
    animeResults: Resource<List<Anime>>,
    mangaResults: Resource<List<com.meigetsu.core.model.Manga>>,
    innerPadding: PaddingValues,
    onMediaClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding)
    ) {
        item { SectionHeader("Anime Results") }
        item {
            MediaHorizontalRow(animeResults, "ANIME", onMediaClick)
        }
        item { SectionHeader("Manga Results") }
        item {
            MediaHorizontalRowManga(mangaResults, "MANGA", onMediaClick)
        }
        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun HomeContent(
    trendingAnime: Resource<List<Anime>>,
    popularAnime: Resource<List<Anime>>,
    trendingManga: Resource<List<com.meigetsu.core.model.Manga>>,
    popularManga: Resource<List<com.meigetsu.core.model.Manga>>,
    recommendedAnime: Resource<List<Anime>>,
    continueWatching: Resource<List<Anime>>,
    topSources: Resource<List<com.meigetsu.core.model.ExternalSource>>,
    innerPadding: PaddingValues,
    onMediaClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())
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

        // Top Sources (Everything Moe)
        item {
            SourcesSection(topSources)
        }

        // Popular Section
        item {
            ModernHomeSection("Must Watch Anime", popularAnime, onMediaClick)
        }

        // Trending Manga
        item {
            ModernHomeSectionManga("Trending Manga", trendingManga, onMediaClick)
        }

        // Popular Manga
        item {
            ModernHomeSectionManga("Popular Manga", popularManga, onMediaClick)
        }

        // Recommended Section
        item {
            ModernHomeSection("For You", recommendedAnime, onMediaClick)
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )
}

@Composable
fun MediaHorizontalRow(
    resource: Resource<List<Anime>>,
    type: String,
    onMediaClick: (String, String) -> Unit
) {
    when (resource) {
        is Resource.Loading -> {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(5) { LoadingSkeleton(Modifier.width(140.dp).height(200.dp).padding(end = 12.dp)) }
            }
        }
        is Resource.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resource.data ?: emptyList()) { item ->
                    MediaCard(
                        title = item.title,
                        imageUrl = item.coverImage,
                        rating = item.rating,
                        onClick = { onMediaClick(item.id, type) }
                    )
                }
            }
        }
        else -> {}
    }
}

@Composable
fun MediaHorizontalRowManga(
    resource: Resource<List<com.meigetsu.core.model.Manga>>,
    type: String,
    onMediaClick: (String, String) -> Unit
) {
    when (resource) {
        is Resource.Loading -> {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(5) { LoadingSkeleton(Modifier.width(140.dp).height(200.dp).padding(end = 12.dp)) }
            }
        }
        is Resource.Success -> {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(resource.data ?: emptyList()) { item ->
                    MediaCard(
                        title = item.title,
                        imageUrl = item.coverImage,
                        rating = item.rating,
                        onClick = { onMediaClick(item.id, type) }
                    )
                }
            }
        }
        else -> {}
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpotlightSection(
    resource: Resource<List<Anime>>,
    onMediaClick: (String, String) -> Unit
) {
    val items = (resource as? Resource.Success)?.data?.take(5) ?: emptyList()
    val pagerState = rememberPagerState { items.size }

    Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
        if (items.isNotEmpty()) {
            HorizontalPager(state = pagerState) { index ->
                val anime = items[index]
                Box(modifier = Modifier.fillMaxSize().clickable { onMediaClick(anime.id, "ANIME") }) {
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
fun ModernHomeSectionManga(
    title: String,
    resource: Resource<List<com.meigetsu.core.model.Manga>>,
    onMediaClick: (String, String) -> Unit
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
                    items(resource.data ?: emptyList()) { manga ->
                        MediaCard(
                            title = manga.title,
                            imageUrl = manga.coverImage,
                            rating = manga.rating,
                            onClick = { onMediaClick(manga.id, "MANGA") }
                        )
                    }
                }
            }
            is Resource.Error -> {
            }
        }
    }
}

@Composable
fun SourcesSection(resource: Resource<List<com.meigetsu.core.model.ExternalSource>>) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        SectionHeader("Discovery Directories")
        when (resource) {
            is Resource.Loading -> {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp)) {
                    items(5) { LoadingSkeleton(Modifier.width(120.dp).height(80.dp).padding(end = 12.dp)) }
                }
            }
            is Resource.Success -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(resource.data?.take(15) ?: emptyList()) { source ->
                        Card(
                            onClick = { uriHandler.openUri(source.url) },
                            modifier = Modifier.width(120.dp).height(80.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Box(modifier = Modifier.fillMaxSize().padding(8.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    if (source.iconUrl != null) {
                                        AsyncImage(
                                            model = source.iconUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(24.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                    Text(
                                        text = source.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = source.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ModernHomeSection(
    title: String,
    resource: Resource<List<Anime>>,
    onMediaClick: (String, String) -> Unit
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
                            onClick = { onMediaClick(anime.id, "ANIME") }
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
