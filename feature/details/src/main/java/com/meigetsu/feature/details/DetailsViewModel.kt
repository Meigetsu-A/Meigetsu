package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.LibraryRepository
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
    private val extensionManager: ExtensionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = checkNotNull(savedStateHandle["mediaId"])

    private val _uiState = MutableStateFlow<Resource<Anime>>(Resource.Loading())
    val uiState: StateFlow<Resource<Anime>> = _uiState.asStateFlow()

    private val _streamUrls = MutableStateFlow<List<StreamUrl>>(emptyList())
    val streamUrls = _streamUrls.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        mediaRepository.getAnimeDetails(mediaId).onEach {
            _uiState.value = it
        }.launchIn(viewModelScope)
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
