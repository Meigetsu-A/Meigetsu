package com.meigetsu.feature.details

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton
import com.meigetsu.core.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit,
    onWatchClick: (String, String) -> Unit,
    onReadClick: (String, String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isInLibrary by viewModel.isInLibrary.collectAsState()
    val streamUrls by viewModel.streamUrls.collectAsState()
    val episodes by viewModel.episodes.collectAsState()
    val chapters by viewModel.chapters.collectAsState()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(streamUrls) {
        if (streamUrls.isNotEmpty()) {
            val anime = (uiState as? Resource.Success)?.data
            onWatchClick(streamUrls.first().url, anime?.id ?: "")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when (val state = uiState) {
            is Resource.Loading -> LoadingSkeleton(Modifier.fillMaxSize())
            is Resource.Success -> {
                val media = state.data!!

                // Immersive Background
                AsyncImage(
                    model = media.coverImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().blur(50.dp),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f
                )

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        MediaHeader(media, onBackClick)
                    }

                    item {
                        MediaActions(
                            media,
                            viewModel,
                            uriHandler,
                            onReadClick,
                            if (media is Anime) episodes.firstOrNull()?.id else chapters.firstOrNull()?.id,
                            isInLibrary
                        )
                    }

                    item {
                        MediaInfo(media)
                    }

                    if (media is Anime && media.characters.isNotEmpty()) {
                        item {
                            CharacterList(media.characters, onCharacterClick)
                        }
                    }

                    item {
                        Text(
                            text = if (media is Manga) "Chapters" else "Episodes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(24.dp)
                        )
                    }

                    if (media is Anime) {
                        items(episodes) { episode ->
                            EpisodeItem(episode, media, onReadClick, viewModel)
                        }
                    } else if (media is Manga) {
                        items(chapters) { chapter ->
                            ChapterItem(chapter, media, onReadClick)
                        }
                    }

                    item { Spacer(Modifier.height(50.dp)) }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message ?: "Error", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun MediaHeader(media: Media, onBackClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
        AsyncImage(
            model = media.bannerImage ?: media.coverImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black))
            )
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.statusBarsPadding().padding(8.dp).background(Color.Black.copy(0.4f), RoundedCornerShape(20.dp))
        ) {
            Icon(Icons.Rounded.ArrowBack, null, tint = Color.White)
        }

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        ) {
            Text(
                text = media.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${media.year ?: ""} • ${media.format.name}", color = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Rounded.Star, null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                Text(" ${media.rating ?: "N/A"}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MediaActions(
    media: Media,
    viewModel: DetailsViewModel,
    uriHandler: androidx.compose.ui.platform.UriHandler,
    onReadClick: (String, String) -> Unit,
    firstItemId: String?,
    isInLibrary: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                if (media is Manga) onReadClick(media.id, firstItemId ?: media.id)
                else viewModel.fetchStreams()
            },
            modifier = Modifier.weight(1f).height(54.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(if (media is Manga) Icons.Rounded.MenuBook else Icons.Rounded.PlayArrow, null)
            Spacer(Modifier.width(8.dp))
            Text(if (media is Manga) "Read Now" else "Watch Now", fontWeight = FontWeight.Bold)
        }

        FilledTonalIconButton(
            onClick = { viewModel.toggleLibrary() },
            modifier = Modifier.size(54.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = if (isInLibrary) Icons.Rounded.Favorite else Icons.Rounded.Add,
                contentDescription = "Toggle Library",
                tint = if (isInLibrary) MaterialTheme.colorScheme.primary else Color.White
            )
        }

        if (media.trailerUrl != null) {
            FilledTonalIconButton(
                onClick = { uriHandler.openUri(media.trailerUrl!!) },
                modifier = Modifier.size(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.SmartDisplay, null)
            }
        }
    }
}

@Composable
fun MediaInfo(media: Media) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = media.description?.replace(Regex("<.*?>"), "") ?: "No description",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(media.genres) { genre ->
                Surface(
                    color = Color.White.copy(0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        genre,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterList(characters: List<com.meigetsu.core.model.Character>, onCharacterClick: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            "Characters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(characters) { char ->
                Column(
                    modifier = Modifier.width(80.dp).clickable { onCharacterClick(char.id) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = char.image,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(40.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(char.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: com.meigetsu.core.model.Episode,
    anime: Anime,
    onReadClick: (String, String) -> Unit,
    viewModel: DetailsViewModel
) {
    ListItem(
        headlineContent = { Text(episode.title ?: "Episode ${episode.number}", fontWeight = FontWeight.Bold) },
        supportingContent = { Text("Episode ${episode.number}", color = Color.Gray) },
        leadingContent = {
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        trailingContent = {
            IconButton(onClick = { /* Download */ }) {
                Icon(Icons.Rounded.Download, null, tint = Color.Gray)
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable {
            viewModel.fetchStreams()
        }
    )
}

@Composable
fun ChapterItem(
    chapter: com.meigetsu.core.model.Chapter,
    manga: Manga,
    onReadClick: (String, String) -> Unit
) {
    ListItem(
        headlineContent = { Text(chapter.title ?: "Chapter ${chapter.number}", fontWeight = FontWeight.Bold) },
        supportingContent = { Text("Chapter ${chapter.number} • ${chapter.scanlator ?: "Unknown"}", color = Color.Gray) },
        leadingContent = {
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.MenuBook, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        trailingContent = {
            IconButton(onClick = { /* Download */ }) {
                Icon(Icons.Rounded.Download, null, tint = Color.Gray)
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier.clickable {
            onReadClick(manga.id, chapter.id)
        }
    )
}
