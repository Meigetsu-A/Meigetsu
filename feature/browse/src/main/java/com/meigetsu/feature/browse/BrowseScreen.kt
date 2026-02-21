package com.meigetsu.feature.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*

@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String, MediaType) -> Unit
) {
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val spotlight by viewModel.spotlight.collectAsState()
    val results by viewModel.results.collectAsState()
    val genres = listOf("Action", "Romance", "Horror", "Fantasy", "Sci-Fi", "Slice of Life", "Mystery", "Sports")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Genre Row
        LazyRow(
            modifier = Modifier.padding(vertical = 16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(genres) { genre ->
                Surface(
                    modifier = Modifier.clickable { viewModel.onGenreSelected(genre) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (selectedGenre == genre) PrimaryText else Color.White.copy(0.05f)
                ) {
                    Text(
                        genre,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = Typography.labelLarge,
                        color = if (selectedGenre == genre) Background else SecondaryText
                    )
                }
            }
        }

        // Spotlight
        spotlight?.let { media ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardNavy)
                    .clickable { onMediaClick(media.id, media.type) }
            ) {
                AsyncImage(
                    model = media.bannerImage ?: media.coverImage,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.4f
                )
                Column(modifier = Modifier.padding(24.dp).align(Alignment.BottomStart)) {
                    Text(media.title, style = Typography.headlineMedium.copy(fontStyle = FontStyle.Italic))
                    Text(media.description ?: "", maxLines = 2, style = Typography.bodyMedium)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Grid Label
        Text(
            text = "${selectedGenre} *Universe*",
            style = Typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(results) { media ->
                MediaPortraitCard(media) { onMediaClick(media.id, media.type) }
            }
        }
    }
}

@Composable
fun MediaPortraitCard(media: Media, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
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
