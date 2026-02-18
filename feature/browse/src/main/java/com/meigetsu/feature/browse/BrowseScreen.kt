package com.meigetsu.feature.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.meigetsu.core.common.Resource
import com.meigetsu.core.extensions.MediaSearchResult
import com.meigetsu.core.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    viewModel: BrowseViewModel,
    onMediaClick: (String, String) -> Unit,
    onCharacterClick: (String) -> Unit
) {
    val animeResults by viewModel.animeResults.collectAsState()
    val mangaResults by viewModel.mangaResults.collectAsState()
    val characterResults by viewModel.characterResults.collectAsState()
    val extensionResults by viewModel.extensionResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val filters by viewModel.filters.collectAsState()
    val externalSources by viewModel.externalSources.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedBrowseTab by remember { mutableStateOf(0) } // 0: Media, 1: Directories

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is BrowseUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filters = filters,
            onFiltersChange = { viewModel.updateFilters(it) },
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(modifier = Modifier.background(Color.Black)) {
                CenterAlignedTopAppBar(
                    title = { Text("BROWSE", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, letterSpacing = 2.sp)) },
                    actions = {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Rounded.FilterList, contentDescription = "Filters", tint = if (filters != BrowseFilters()) MaterialTheme.colorScheme.primary else Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )

                TabRow(
                    selectedTabIndex = selectedBrowseTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = {}
                ) {
                    Tab(selected = selectedBrowseTab == 0, onClick = { selectedBrowseTab = 0 }, text = { Text("Universal") })
                    Tab(selected = selectedBrowseTab == 1, onClick = { selectedBrowseTab = 1 }, text = { Text("Directories") })
                }

                if (selectedBrowseTab == 0) {
                    SearchBar(
                        query = filters.query,
                        onQueryChange = { viewModel.updateQuery(it) },
                        onSearch = { viewModel.search() },
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Anime, Manga, Characters...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = SearchBarDefaults.colors(containerColor = Color(0xFF141414))
                    ) { }

                    // Quick Genre Row
                    val genres = listOf("Action", "Adventure", "Comedy", "Drama", "Fantasy", "Romance", "Sci-Fi", "Slice of Life")
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(genres) { genre ->
                            FilterChip(
                                selected = filters.genre == genre,
                                onClick = { viewModel.updateGenre(if (filters.genre == genre) null else genre) },
                                label = { Text(genre) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.Black
    ) { innerPadding ->
        if (selectedBrowseTab == 0) {
            UnifiedSearchResults(
                animeResults,
                mangaResults,
                characterResults,
                extensionResults,
                isSearching,
                innerPadding,
                onMediaClick,
                onCharacterClick,
                onLoadMore = { viewModel.loadNextPage() }
            )
        } else {
            ExternalSourcesContent(externalSources, innerPadding)
        }
    }
}

@Composable
fun ExternalSourcesContent(
    resource: Resource<List<com.meigetsu.core.model.ExternalSource>>,
    innerPadding: PaddingValues
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    when (resource) {
        is Resource.Loading -> {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            val sources = resource.data ?: emptyList()
            val categories = sources.groupBy { it.category }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                categories.forEach { (category, sites) ->
                    item {
                        SearchSectionHeader(category)
                    }
                    items(sites.chunked(2)) { chunk ->
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            chunk.forEach { site ->
                                Card(
                                    onClick = { uriHandler.openUri(site.url) },
                                    modifier = Modifier.weight(1f).height(60.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (site.iconUrl != null) {
                                            AsyncImage(
                                                model = site.iconUrl,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp))
                                            )
                                            Spacer(Modifier.width(12.dp))
                                        }
                                        Text(site.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                            }
                            if (chunk.size < 2) Spacer(Modifier.weight(1f))
                        }
                    }
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }
        is Resource.Error -> {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Failed to load directories: ${resource.message}", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filters: BrowseFilters,
    onFiltersChange: (BrowseFilters) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
        ) {
            Text("Filters & Sorting", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(24.dp))

            // Sort
            Text("Sort By", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            val sortOptions = mapOf(
                "TRENDING_DESC" to "Trending",
                "POPULARITY_DESC" to "Popularity",
                "SCORE_DESC" to "Score",
                "START_DATE_DESC" to "Newest",
                "TITLE_ROMAJI" to "Alphabetical"
            )
            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                sortOptions.forEach { (key, value) ->
                    FilterChip(
                        selected = filters.sort == key,
                        onClick = { onFiltersChange(filters.copy(sort = key)) },
                        label = { Text(value) }
                    )
                }
            }

            // Status
            Text("Status", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            val statusOptions = listOf("RELEASING", "FINISHED", "NOT_YET_RELEASED", "CANCELLED", "HIATUS")
            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statusOptions.forEach { status ->
                    FilterChip(
                        selected = filters.status == status,
                        onClick = { onFiltersChange(filters.copy(status = if (filters.status == status) null else status)) },
                        label = { Text(status.replace("_", " ")) }
                    )
                }
            }

            // Format
            Text("Format", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            val formatOptions = listOf("TV", "MOVIE", "SPECIAL", "OVA", "ONA", "MUSIC", "MANGA", "NOVEL", "ONE_SHOT")
            FlowRow(modifier = Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                formatOptions.forEach { format ->
                    FilterChip(
                        selected = filters.format == format,
                        onClick = { onFiltersChange(filters.copy(format = if (filters.format == format) null else format)) },
                        label = { Text(format.replace("_", " ")) }
                    )
                }
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Filters", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            TextButton(
                onClick = { onFiltersChange(BrowseFilters()); onDismiss() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset All", color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}

@Composable
fun UnifiedSearchResults(
    anime: Resource<List<com.meigetsu.core.model.Anime>>,
    manga: Resource<List<com.meigetsu.core.model.Manga>>,
    characters: Resource<List<com.meigetsu.core.model.Character>>,
    extensions: List<MediaSearchResult>,
    isSearching: Boolean,
    innerPadding: PaddingValues,
    onMediaClick: (String, String) -> Unit,
    onCharacterClick: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    if (isSearching) {
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        // Anime Section
        if (anime is Resource.Success && !anime.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Anime Results") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(anime.data!!) { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.coverImage,
                            modifier = Modifier.width(140.dp),
                            onClick = { onMediaClick(item.id, "ANIME") }
                        )
                    }
                    item {
                        Box(modifier = Modifier.width(100.dp).fillMaxHeight().clickable { onLoadMore() }, contentAlignment = Alignment.Center) {
                            Text("Load More", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Manga Section
        if (manga is Resource.Success && !manga.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Manga Results") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(manga.data!!) { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.coverImage,
                            modifier = Modifier.width(140.dp),
                            onClick = { onMediaClick(item.id, "MANGA") }
                        )
                    }
                    item {
                        Box(modifier = Modifier.width(100.dp).fillMaxHeight().clickable { onLoadMore() }, contentAlignment = Alignment.Center) {
                            Text("Load More", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Characters Section
        if (characters is Resource.Success && !characters.data.isNullOrEmpty()) {
            item { SearchSectionHeader("Characters") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                    items(characters.data!!) { item ->
                        MediaCard(
                            title = item.name,
                            imageUrl = item.image,
                            modifier = Modifier.width(140.dp),
                            onClick = { onCharacterClick(item.id) }
                        )
                    }
                }
            }
        }

        // Extension Results
        if (extensions.isNotEmpty()) {
            item { SearchSectionHeader("Other Sources") }
            items(extensions.chunked(3)) { chunk ->
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    chunk.forEach { item ->
                        MediaCard(
                            title = item.title,
                            imageUrl = item.imageUrl,
                            type = item.type,
                            modifier = Modifier.weight(1f).padding(4.dp),
                            onClick = {
                                if (item.type.uppercase() == "CHARACTER") {
                                    onCharacterClick(item.id)
                                } else {
                                    onMediaClick(item.id, item.type.uppercase())
                                }
                            }
                        )
                    }
                    if (chunk.size < 3) {
                        repeat(3 - chunk.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(16.dp),
        color = Color.White
    )
}
