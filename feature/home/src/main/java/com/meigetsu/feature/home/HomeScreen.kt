package com.meigetsu.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
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
    val featured by viewModel.featuredMedia.collectAsState()
    val trending by viewModel.trendingMedia.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val continueReading by viewModel.continueReading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        item { HomeTopBar() }
        item { GreetingSection() }
        item { CategoryFilterPills() }

        featured?.let {
            item { FeaturedCard(it) { onMediaClick(it.id, it.type) } }
        }

        item { SectionHeader("Continue *Watching*", "See all") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(continueWatching) { media ->
                    ContinueWatchingCard(media) { onMediaClick(media.id, media.type) }
                }
            }
        }

        item { SectionHeader("Continue *Reading*", "See all") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(continueReading) { media ->
                    ContinueReadingCard(media) { onMediaClick(media.id, media.type) }
                }
            }
        }

        item { SectionHeader("Trending *This Week*", "See all") }
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trending) { media ->
                    MediaPortraitCard(media) { onMediaClick(media.id, media.type) }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "MEIGETSU",
            style = Typography.titleLarge,
            color = PrimaryText
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                modifier = Modifier.size(36.dp).clickable { /* Navigate to search */ },
                shape = CircleShape,
                color = Color.White.copy(0.05f)
            ) {
                Icon(Icons.Rounded.Search, null, modifier = Modifier.padding(8.dp), tint = PrimaryText)
            }
            Surface(
                modifier = Modifier.size(36.dp).clickable { /* Show notifications */ },
                shape = CircleShape,
                color = Color.White.copy(0.05f)
            ) {
                Icon(Icons.Rounded.Notifications, null, modifier = Modifier.padding(8.dp), tint = PrimaryText)
            }
        }
    }
}

@Composable
fun GreetingSection() {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text("Good evening", style = Typography.labelSmall)
        Spacer(Modifier.height(4.dp))
        Text(
            text = "What will you\n*discover* today?",
            style = Typography.headlineLarge,
            lineHeight = 36.sp
        )
    }
}

@Composable
fun CategoryFilterPills() {
    val categories = listOf("All", "Anime", "Manga", "Novels")
    var selected by remember { mutableStateOf("All") }
    LazyRow(
        modifier = Modifier.padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            Surface(
                modifier = Modifier.clickable { selected = cat },
                shape = RoundedCornerShape(20.dp),
                color = if (selected == cat) PrimaryText else Color.White.copy(0.05f)
            ) {
                Text(
                    cat,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    style = Typography.labelLarge,
                    color = if (selected == cat) Background else SecondaryText
                )
            }
        }
    }
}

@Composable
fun FeaturedCard(media: Media, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(CardAmber, CardBrown)))
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(20.dp).align(Alignment.TopStart)) {
            Text("CURRENTLY WATCHING", style = Typography.labelSmall, color = Color.White.copy(0.6f))
            Spacer(Modifier.height(4.dp))
            Text(media.title, style = Typography.headlineMedium.copy(fontStyle = FontStyle.Italic))
            Text("Episode 12 of 24", style = Typography.bodyLarge)
        }
        Surface(
            modifier = Modifier.padding(20.dp).size(48.dp).align(Alignment.BottomEnd),
            shape = CircleShape,
            color = PrimaryText
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = null,
                tint = Background,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val parts = title.split("*")
        Row {
            parts.forEachIndexed { index, s ->
                Text(
                    s,
                    style = if (index % 2 == 1) Typography.bodyLarge.copy(fontStyle = FontStyle.Italic) else Typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(action, style = Typography.labelLarge, color = SecondaryText)
    }
}

@Composable
fun ContinueWatchingCard(media: Media, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(144.dp, 82.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CardNavy)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = media.bannerImage ?: media.coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .background(Color.Black.copy(0.7f), RoundedCornerShape(2.dp))
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text("EP 08", style = Typography.labelSmall, color = Color.White, fontSize = 9.sp)
        }
    }
}

@Composable
fun ContinueReadingCard(media: Media, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(88.dp, 122.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(CardMauve)
        ) {
            AsyncImage(
                model = media.coverImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                media.type.name,
                modifier = Modifier.padding(4.dp).background(Color.Black.copy(0.7f)).padding(2.dp),
                style = Typography.labelSmall,
                fontSize = 8.sp
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.White.copy(0.1f))) {
            Box(modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight().background(PrimaryText))
        }
    }
}

@Composable
fun MediaPortraitCard(media: Media, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(88.dp, 122.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(CardColors.random())
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = media.coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
