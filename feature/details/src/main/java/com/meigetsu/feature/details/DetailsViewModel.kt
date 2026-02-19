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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailsUiEvent {
    data class ShowSnackbar(val message: String) : DetailsUiEvent()
}

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val libraryRepository: LibraryRepository,
    private val downloadItemsUseCase: DownloadItemsUseCase,
    private val extensionManager: ExtensionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String? = savedStateHandle["mediaId"]
    private val malId: Int? = savedStateHandle["malId"]
    private val mediaType: String? = savedStateHandle["mediaType"]

    private val _uiState = MutableStateFlow<Resource<Media>>(Resource.Loading())
    val uiState: StateFlow<Resource<Media>> = _uiState.asStateFlow()

    private val _isInLibrary = MutableStateFlow(false)
    val isInLibrary = _isInLibrary.asStateFlow()

    private val _streamUrls = MutableStateFlow<List<StreamUrl>>(emptyList())
    val streamUrls = _streamUrls.asStateFlow()

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters = _chapters.asStateFlow()

    private val _selectedEpisodeIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedEpisodeIds = _selectedEpisodeIds.asStateFlow()

    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    private val _eventChannel = Channel<DetailsUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    init {
        loadDetails()
        checkIfInLibrary()
    }

    private fun loadDetails() {
        if (mediaType == "MANGA") {
            mediaRepository.getMangaDetails(id = mediaId, idMal = malId).onEach { resource ->
                _uiState.value = resource
                if (resource is Resource.Success) {
                    fetchChapters(resource.data!!)
                }
            }.launchIn(viewModelScope)
        } else {
            mediaRepository.getAnimeDetails(id = mediaId, idMal = malId).onEach { resource ->
                _uiState.value = resource
                if (resource is Resource.Success) {
                    fetchEpisodes(resource.data!!)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun checkIfInLibrary() {
        combine(
            libraryRepository.getLibraryAnime(),
            libraryRepository.getLibraryManga()
        ) { anime, manga ->
            val id = (_uiState.value as? Resource.Success)?.data?.id
            anime.any { it.id == id } || manga.any { it.id == id }
        }.onEach { _isInLibrary.value = it }.launchIn(viewModelScope)
    }

    private fun fetchEpisodes(anime: Anime) {
        viewModelScope.launch {
            val providers = extensionManager.animeProviders.value.values
            val provider = providers.firstOrNull { it.metadata.id != "anilist" }
                ?: providers.firstOrNull()

            if (provider != null) {
                // Try searching by title first to get the provider-specific ID
                val searchResults = provider.search(anime.title, 1)
                val bestMatch = searchResults.find { it.title.equals(anime.title, ignoreCase = true) }
                    ?: searchResults.firstOrNull()

                val providerId = bestMatch?.id ?: anime.id
                _episodes.value = provider.getEpisodes(providerId)
            }
        }
    }

    private fun fetchChapters(manga: Manga) {
        viewModelScope.launch {
            val providers = extensionManager.mangaProviders.value.values
            val provider = providers.firstOrNull { it.metadata.id != "anilist" }
                ?: providers.firstOrNull()

            if (provider != null) {
                val searchResults = provider.search(manga.title, 1)
                val bestMatch = searchResults.find { it.title.equals(manga.title, ignoreCase = true) }
                    ?: searchResults.firstOrNull()

                val providerId = bestMatch?.id ?: manga.id
                _chapters.value = provider.getChapters(providerId)
            }
        }
    }

    fun toggleLibrary() {
        viewModelScope.launch {
            val media = (uiState.value as? Resource.Success)?.data ?: return@launch
            if (_isInLibrary.value) {
                libraryRepository.removeFromLibrary(media.id)
                _eventChannel.send(DetailsUiEvent.ShowSnackbar("Removed from Library"))
            } else {
                if (media is Anime) libraryRepository.addToLibrary(media)
                else if (media is Manga) libraryRepository.addToLibrary(media)
                _eventChannel.send(DetailsUiEvent.ShowSnackbar("Added to Library"))
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
            val media = (uiState.value as? Resource.Success)?.data ?: return@launch

            if (media is Anime) {
                val itemsToDownload = episodes.value.filter { selectedEpisodeIds.value.contains(it.id) }
                val providers = extensionManager.animeProviders.value
                val providerId = providers.keys.find { it != "anilist" }
                    ?: providers.keys.firstOrNull() ?: return@launch

                downloadItemsUseCase.execute(media, itemsToDownload, providerId)
                _eventChannel.send(DetailsUiEvent.ShowSnackbar("Added ${itemsToDownload.size} episodes to downloads"))
            } else if (media is Manga) {
                val itemsToDownload = chapters.value.filter { selectedEpisodeIds.value.contains(it.id) }
                val providers = extensionManager.mangaProviders.value
                val providerId = providers.keys.find { it != "anilist" }
                    ?: providers.keys.firstOrNull() ?: return@launch

                downloadItemsUseCase.executeChapters(media, itemsToDownload, providerId)
                _eventChannel.send(DetailsUiEvent.ShowSnackbar("Added ${itemsToDownload.size} chapters to downloads"))
            }

            clearSelection()
        }
    }

    fun fetchStreams(episode: Episode) {
        viewModelScope.launch {
            val providers = extensionManager.animeProviders.value.values
            val provider = providers.find { it.metadata.id != "anilist" }
                ?: providers.firstOrNull()

            provider?.let {
                _streamUrls.value = it.getStreamUrls(episode)
            }
        }
    }

    fun addToLibrary() {
        toggleLibrary()
    }
}
