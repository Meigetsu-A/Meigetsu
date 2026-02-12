package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.usecase.DownloadItemsUseCase
import com.meigetsu.core.model.*
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.StreamUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val libraryRepository: LibraryRepository,
    private val downloadItemsUseCase: DownloadItemsUseCase,
    private val extensionManager: ExtensionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = checkNotNull(savedStateHandle["mediaId"])

    private val _uiState = MutableStateFlow<Resource<Anime>>(Resource.Loading())
    val uiState: StateFlow<Resource<Anime>> = _uiState.asStateFlow()

    private val _streamUrls = MutableStateFlow<List<StreamUrl>>(emptyList())
    val streamUrls = _streamUrls.asStateFlow()

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    private val _selectedEpisodeIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedEpisodeIds = _selectedEpisodeIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        mediaRepository.getAnimeDetails(mediaId).onEach { resource ->
            _uiState.value = resource
            if (resource is Resource.Success) {
                fetchEpisodes(resource.data!!)
            }
        }.launchIn(viewModelScope)
    }

    private fun fetchEpisodes(anime: Anime) {
        viewModelScope.launch {
            // Find a provider that has episodes for this anime (simulated)
            val provider = extensionManager.animeProviders.value.values.firstOrNull()
            if (provider != null) {
                _episodes.value = provider.getEpisodes(anime.id)
            }
        }
    }

    fun toggleSelection(episodeId: String) {
        _selectedEpisodeIds.value = if (_selectedEpisodeIds.value.contains(episodeId)) {
            _selectedEpisodeIds.value - episodeId
        } else {
            _selectedEpisodeIds.value + episodeId
        }
        _isSelectionMode.value = _selectedEpisodeIds.value.isNotEmpty()
    }

    fun clearSelection() {
        _selectedEpisodeIds.value = emptySet()
        _isSelectionMode.value = false
    }

    fun downloadSelected() {
        viewModelScope.launch {
            val anime = (uiState.value as? Resource.Success)?.data ?: return@launch
            val itemsToDownload = episodes.value.filter { selectedEpisodeIds.value.contains(it.id) }
            val providerId = extensionManager.animeProviders.value.keys.firstOrNull() ?: return@launch

            downloadItemsUseCase.execute(anime, itemsToDownload, providerId)
            clearSelection()
        }
    }

    fun fetchStreams() {
        viewModelScope.launch {
            val anime = (uiState.value as? Resource.Success)?.data ?: return@launch
            // Try to find a provider that can handle this anime
            extensionManager.animeProviders.value.values.firstOrNull()?.let { provider ->
                _streamUrls.value = provider.getStreamUrls(Episode("1", anime.id, 1, "Episode 1", null, null))
            }
        }
    }

    fun addToLibrary() {
        viewModelScope.launch {
            val anime = (uiState.value as? Resource.Success)?.data ?: return@launch
            libraryRepository.addToLibrary(anime)
        }
    }
}
