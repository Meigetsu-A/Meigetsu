package com.meigetsu.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.components.MediaCard
import com.meigetsu.core.ui.theme.*

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onMediaClick: (String, MediaType) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        // Underline Search Input
        Box(modifier = Modifier.fillMaxWidth()) {
            BasicTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                textStyle = Typography.headlineMedium.copy(color = PrimaryText, fontStyle = FontStyle.Italic),
                cursorBrush = SolidColor(PrimaryText),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Column {
                        if (query.isEmpty()) {
                            Text(
                                "Search titles, genres…",
                                style = Typography.headlineMedium.copy(color = MutedText, fontStyle = FontStyle.Italic)
                            )
                        }
                        innerTextField()
                        Spacer(Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(SecondaryText))
                    }
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        if (query.isEmpty()) {
            Text("Recent Searches", style = Typography.labelLarge)
            Spacer(Modifier.height(16.dp))
            LazyColumn {
                items(recentSearches) { search ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(search, style = Typography.bodyLarge, modifier = Modifier.clickable { viewModel.onQueryChange(search) })
                        Icon(Icons.Rounded.Close, null, tint = MutedText, modifier = Modifier.size(16.dp).clickable { viewModel.removeRecentSearch(search) })
                    }
                }
            }

            if (recentSearches.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "*\"In the silence of the search,\none finds the unexpected.\"*",
                        style = Typography.headlineMedium.copy(color = MutedText, fontStyle = FontStyle.Italic),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            // Results Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { media ->
                    MediaCard(
                        title = media.title,
                        imageUrl = media.coverImage,
                        rating = media.score?.toDouble(),
                        type = media.type.name,
                        onClick = { onMediaClick(media.id, media.type) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
