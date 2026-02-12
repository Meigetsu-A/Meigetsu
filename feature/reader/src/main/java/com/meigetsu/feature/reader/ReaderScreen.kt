package com.meigetsu.feature.reader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel
) {
    val pages by viewModel.pages.collectAsState()
    var mode by remember { mutableStateOf(ReaderMode.VERTICAL) }
    var isSharpenEnabled by remember { mutableStateOf(false) }

    val pagerState = rememberPagerState { pages.size }

    Box(modifier = Modifier.fillMaxSize()) {
    when (mode) {
        ReaderMode.VERTICAL, ReaderMode.WEBTOON -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(pages) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillParentMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
        ReaderMode.HORIZONTAL -> {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                AsyncImage(
                    model = pages[index],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    // Filter Toggle
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = androidx.compose.ui.Alignment.BottomEnd) {
        FilledTonalIconToggleButton(
            checked = isSharpenEnabled,
            onCheckedChange = { isSharpenEnabled = it }
        ) {
            Icon(Icons.Rounded.AutoFixHigh, contentDescription = "Sharpen Filter")
        }
    }
    }
}
