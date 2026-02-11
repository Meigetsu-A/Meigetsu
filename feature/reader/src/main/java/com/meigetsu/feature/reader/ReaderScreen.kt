package com.meigetsu.feature.reader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel
) {
    val pages by viewModel.pages.collectAsState()
    var mode by remember { mutableStateOf(ReaderMode.VERTICAL) }

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
            LazyRow(modifier = Modifier.fillMaxSize()) {
                items(pages) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.fillParentMaxHeight(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
