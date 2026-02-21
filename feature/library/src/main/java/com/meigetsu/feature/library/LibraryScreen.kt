package com.meigetsu.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onMediaClick: (String, MediaType) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("WATCHING", "READING", "DOWNLOADED")
    val items by viewModel.libraryItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                Text(
                    tab,
                    modifier = Modifier.clickable { selectedTab = index },
                    style = Typography.labelLarge,
                    color = if (selectedTab == index) PrimaryText else MutedText,
                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "*\"A library is a collection\nof silent friends.\"*",
                    style = Typography.headlineMedium.copy(color = MutedText, fontStyle = FontStyle.Italic),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(items) { media ->
                    LibraryRow(media) { onMediaClick(media.id, media.type) }
                }
            }
        }
    }
}

@Composable
fun LibraryRow(media: Media, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(48.dp)
                .background(CardColors.random())
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(media.title, style = Typography.bodyLarge)
            Text("EP 08 • Ongoing", style = Typography.labelSmall)
        }
        Surface(
            shape = RoundedCornerShape(2.dp),
            color = Color.White.copy(0.05f)
        ) {
            Text(
                media.status.name,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = Typography.labelSmall,
                fontSize = 9.sp
            )
        }
    }
}
