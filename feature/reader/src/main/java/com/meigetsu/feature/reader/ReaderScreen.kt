package com.meigetsu.feature.reader

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var showControls by remember { mutableStateOf(false) } // Default hidden for immersion
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
                    modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(onTap = { showControls = !showControls })
                    },
                    state = listState
                ) {
                    itemsIndexed(pages) { index, url ->
                        ZoomableImage(
                            url = url,
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
                    modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                        detectTapGestures(onTap = { showControls = !showControls })
                    },
                    pageSpacing = 8.dp
                ) { index ->
                    ZoomableImage(
                        url = pages[index],
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // Minimalist Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            TopAppBar(
                title = { Text("Chapter ${currentPage + 1}", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(0.6f),
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
                color = Color.Black.copy(alpha = 0.8f),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp).navigationBarsPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${currentPage + 1} / ${pages.size}", style = MaterialTheme.typography.labelLarge)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            IconButton(onClick = { showSettings = true }) {
                                Icon(Icons.Rounded.Settings, null)
                            }
                            IconButton(onClick = { /* Bookmark */ }) {
                                Icon(Icons.Rounded.BookmarkBorder, null)
                            }
                        }
                    }
                    Slider(
                        value = if (pages.isNotEmpty()) (currentPage.toFloat() / (pages.size - 1).coerceAtLeast(1)) else 0f,
                        onValueChange = { /* Seek logic */ },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Settings Sheet
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false },
                containerColor = Color(0xFF141414),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    Text("Reading Mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        ReaderModeItem("Vertical", Icons.Rounded.Expand, mode == ReaderMode.VERTICAL) { mode = ReaderMode.VERTICAL }
                        ReaderModeItem("Horizontal", Icons.Rounded.SwapHoriz, mode == ReaderMode.HORIZONTAL) { mode = ReaderMode.HORIZONTAL }
                        ReaderModeItem("Webtoon", Icons.Rounded.ViewStream, mode == ReaderMode.WEBTOON) { mode = ReaderMode.WEBTOON }
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomableImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset += offsetChange
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 3f
                        offset = Offset.Zero
                    }
                )
            }
            .transformable(state = state)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale
        )
    }
}

@Composable
fun ReaderModeItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(icon, null, tint = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
