package com.meigetsu.feature.reader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.model.*
import com.meigetsu.core.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel,
    onBackClick: () -> Unit
) {
    val pages by viewModel.pages.collectAsState()
    var isWebtoonMode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        if (pages.firstOrNull()?.startsWith("text://") == true) {
            // Novel Mode
            val textContent = pages.first().replace("text://", "")
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                        .padding(top = 60.dp, bottom = 80.dp)
                ) {
                    Text(
                        text = android.text.Html.fromHtml(textContent, android.text.Html.FROM_HTML_MODE_COMPACT).toString(),
                        style = Typography.bodyLarge,
                        lineHeight = 28.sp
                    )
                }
            }
        } else if (isWebtoonMode) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(pages) { page ->
                    AsyncImage(
                        model = page,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        } else {
            val pagerState = rememberPagerState { pages.size }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                AsyncImage(
                    model = pages[pageIndex],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(0.7f))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = PrimaryText)
                }
                Text("Chapter 01", style = Typography.labelLarge)
            }
            Text(
                if (isWebtoonMode) "WEBTOON" else "PAGED",
                modifier = Modifier.clickable { isWebtoonMode = !isWebtoonMode },
                style = Typography.labelLarge,
                color = PrimaryText
            )
        }

        // Bottom Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(0.7f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("1 / ${pages.size}", style = Typography.labelSmall)
        }
    }
}
