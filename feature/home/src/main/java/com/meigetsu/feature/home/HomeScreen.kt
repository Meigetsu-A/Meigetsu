package com.meigetsu.feature.home
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMediaClick: (String, MediaType) -> Unit
) {
    val featuredMedia by viewModel.featuredMedia.collectAsState()
    val trendingAnime by viewModel.trendingAnime.collectAsState()
    val popularManga by viewModel.popularManga.collectAsState()
    val upcomingAnime by viewModel.upcomingAnime.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val continueReading by viewModel.continueReading.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        item {
            FeaturedCard(featuredMedia, onMediaClick)
        }
        if (continueWatching.isNotEmpty()) {
            item { MediaSection("Continue Watching", continueWatching, onMediaClick) }
        }
        if (continueReading.isNotEmpty()) {
            item { MediaSection("Continue Reading", continueReading, onMediaClick) }
        }
        item { MediaSection("Trending Anime", trendingAnime, onMediaClick) }
        item { MediaSection("Popular Manga", popularManga, onMediaClick) }
        item { MediaSection("Upcoming Anime", upcomingAnime, onMediaClick) }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
@Composable
fun FeaturedCard(media: Media?, onMediaClick: (String, MediaType) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(450.dp).clickable { media?.let { onMediaClick(it.id, it.type) } }
    ) {
        AsyncImage(
            model = media?.bannerImage ?: media?.coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Background),
                    startY = 300f
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        ) {
            Text(media?.title ?: "Loading...", style = Typography.headlineLarge, fontSize = 32.sp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                Text(media?.genres?.take(2)?.joinToString(" • ") ?: "", style = Typography.labelSmall)
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Rounded.PlayArrow, null, tint = PrimaryText, modifier = Modifier.size(16.dp))
            }
        }
    }
}
@Composable
fun MediaSection(title: String, items: List<Media>, onMediaClick: (String, MediaType) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(title.uppercase(), style = Typography.titleLarge, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { media ->
                MediaCard(media, onMediaClick)
            }
        }
    }
}
@Composable
fun MediaCard(media: Media, onMediaClick: (String, MediaType) -> Unit) {
    Column(
        modifier = Modifier.width(140.dp).clickable { onMediaClick(media.id, media.type) }
    ) {
        AsyncImage(
            model = media.coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(200.dp).background(Color(0xFF1A1A1A)),
            contentScale = ContentScale.Crop
        )
        Text(media.title, style = Typography.bodyMedium, maxLines = 2, modifier = Modifier.padding(top = 8.dp))
        Text(media.year?.toString() ?: "", style = Typography.labelSmall)
    }
}
