package com.meigetsu.feature.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meigetsu.core.network.JikanAnimeData
import com.meigetsu.core.ui.components.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onMediaClick: (malId: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDay by remember { mutableStateOf("Monday") }

    Scaffold(
        topBar = {
            Column {
                LargeTopAppBar(title = { Text("Schedule", fontWeight = FontWeight.Bold) })
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(days) { day ->
                        FilterChip(
                            selected = selectedDay == day,
                            onClick = {
                                selectedDay = day
                                viewModel.loadSchedule(day.lowercase())
                            },
                            label = { Text(day) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is ScheduleUiState.Loading -> LoadingSkeleton(Modifier.fillMaxSize())
                is ScheduleUiState.Success -> {
                    ScheduleList(state.anime, onMediaClick)
                }
                is ScheduleUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ScheduleList(anime: List<JikanAnimeData>, onMediaClick: (Int) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(anime) { item ->
            ScheduleItem(item, onMediaClick)
        }
    }
}

@Composable
fun ScheduleItem(item: JikanAnimeData, onMediaClick: (Int) -> Unit) {
    ElevatedCard(
        onClick = { onMediaClick(item.mal_id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.height(120.dp)) {
            AsyncImage(
                model = item.images.jpg.image_url,
                contentDescription = null,
                modifier = Modifier.width(90.dp).fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.broadcast?.string ?: "Unknown Time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
