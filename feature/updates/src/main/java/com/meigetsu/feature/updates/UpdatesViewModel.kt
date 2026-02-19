package com.meigetsu.feature.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.repository.PreferenceRepository
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val extensionManager: ExtensionManager,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val _updates = MutableStateFlow<List<UpdateItem>>(emptyList())
    val updates: StateFlow<List<UpdateItem>> = _updates.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        loadUpdates()
        observeAutoRefresh()
    }

    private fun observeAutoRefresh() {
        preferenceRepository.getAutoRefreshInterval().onEach { minutes ->
            autoRefreshJob?.cancel()
            if (minutes > 0) {
                autoRefreshJob = viewModelScope.launch {
                    while (true) {
                        delay(minutes * 60 * 1000L)
                        loadUpdates()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadUpdates() {
        viewModelScope.launch {
            val anime = libraryRepository.getLibraryAnime().first()
            val manga = libraryRepository.getLibraryManga().first()

            val newUpdates = mutableListOf<UpdateItem>()

            anime.forEach { item ->
                launch {
                    val provider = extensionManager.animeProviders.value.values.firstOrNull { it.metadata.id != "anilist" }
                    provider?.let { p ->
                        try {
                            val searchResults = p.search(item.title, 1)
                            val bestMatch = searchResults.find { it.title.equals(item.title, ignoreCase = true) }
                            bestMatch?.let {
                                val episodes = p.getEpisodes(it.id)
                                if (item.episodes != null && episodes.size > item.episodes!!) {
                                    newUpdates.add(UpdateItem(
                                        id = item.id,
                                        title = item.title,
                                        updateInfo = "New Episode: ${episodes.lastOrNull()?.number ?: ""}",
                                        timestamp = System.currentTimeMillis(),
                                        imageUrl = item.coverImage
                                    ))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            manga.forEach { item ->
                launch {
                    val provider = extensionManager.mangaProviders.value.values.firstOrNull { it.metadata.id != "anilist" }
                    provider?.let { p ->
                        try {
                            val searchResults = p.search(item.title, 1)
                            val bestMatch = searchResults.find { it.title.equals(item.title, ignoreCase = true) }
                            bestMatch?.let {
                                val chapters = p.getChapters(it.id)
                                if (item.chapters != null && chapters.size > item.chapters!!) {
                                    newUpdates.add(UpdateItem(
                                        id = item.id,
                                        title = item.title,
                                        updateInfo = "New Chapter: ${chapters.lastOrNull()?.number ?: ""}",
                                        timestamp = System.currentTimeMillis(),
                                        imageUrl = item.coverImage
                                    ))
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }

            // Wait for all launches to finish (simplified)
            delay(2000)
            _updates.value = newUpdates.sortedByDescending { it.timestamp }
        }
    }
}

data class UpdateItem(
    val id: String,
    val title: String,
    val updateInfo: String,
    val timestamp: Long,
    val imageUrl: String? = null
)
