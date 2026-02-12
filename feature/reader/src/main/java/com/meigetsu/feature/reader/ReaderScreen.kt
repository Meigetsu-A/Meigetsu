package com.meigetsu.feature.reader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel
) {
    val pages by viewModel.pages.collectAsState()
    var mode by remember { mutableStateOf(ReaderMode.VERTICAL) }

    val pagerState = rememberPagerState { pages.size }

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
}
