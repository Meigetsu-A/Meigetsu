package com.meigetsu.feature.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.ui.components.LoadingSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel
) {
    val newsState by viewModel.news.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("Anime News", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        when (val state = newsState) {
            is Resource.Loading -> {
                Column(modifier = Modifier.padding(innerPadding)) {
                    repeat(5) { LoadingSkeleton(Modifier.fillMaxWidth().height(120.dp).padding(16.dp)) }
                }
            }
            is Resource.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                    items(state.data ?: emptyList()) { article ->
                        NewsCard(article = article) {
                            uriHandler.openUri(article.url)
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(text = state.message ?: "Error loading news")
                }
            }
        }
    }
}

@Composable
fun NewsCard(
    article: com.meigetsu.core.model.NewsArticle,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() }
    ) {
        Column {
            if (article.imageUrl != null) {
                AsyncImage(
                    model = article.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = article.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = article.description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = article.source, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(text = article.date, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
