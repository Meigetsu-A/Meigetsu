package com.meigetsu.feature.reader

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit
) {
    val pages by viewModel.pages.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()

    var mode by remember { mutableStateOf(ReaderMode.WEBTOON) }
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState { pages.size }
    val listState = rememberLazyListState()

    LaunchedEffect(pagerState.currentPage) {
        if (mode == ReaderMode.HORIZONTAL) {
            viewModel.onPageChanged(pagerState.currentPage)
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (mode != ReaderMode.HORIZONTAL) {
            viewModel.onPageChanged(listState.firstVisibleItemIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        when (mode) {
            ReaderMode.VERTICAL, ReaderMode.WEBTOON -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls },
                    state = listState
                ) {
                    itemsIndexed(pages) { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Page ${index + 1}",
                            modifier = Modifier.fillParentMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                        if (mode == ReaderMode.VERTICAL) {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            ReaderMode.HORIZONTAL -> {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls },
                    pageSpacing = 8.dp
                ) { index ->
                    AsyncImage(
                        model = pages[index],
                        contentDescription = "Page ${index + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // Controls
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = { Text("Chapter Reading", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.6f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }

        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${currentPage + 1} / ${pages.size}", style = MaterialTheme.typography.labelLarge)
                        Row {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
                            }
                            IconButton(onClick = { /* Bookmark */ }) {
                                Icon(Icons.Rounded.BookmarkBorder, contentDescription = "Bookmark")
                            }
                        }
                    }
                    Slider(
                        value = if (pages.isNotEmpty()) currentPage.toFloat() / (pages.size - 1) else 0f,
                        onValueChange = { /* Seek */ },
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                    )
                }
            }
        }

        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    Text(text = "Reading Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ReaderModeItem("Vertical", Icons.Rounded.Expand, mode == ReaderMode.VERTICAL) { mode = ReaderMode.VERTICAL }
                        ReaderModeItem("Horizontal", Icons.Rounded.SwapHoriz, mode == ReaderMode.HORIZONTAL) { mode = ReaderMode.HORIZONTAL }
                        ReaderModeItem("Webtoon", Icons.Rounded.ViewStream, mode == ReaderMode.WEBTOON) { mode = ReaderMode.WEBTOON }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "Display", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ListItem(
                        headlineContent = { Text("Immersive Mode") },
                        trailingContent = { Switch(checked = true, onCheckedChange = {}) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReaderModeItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
