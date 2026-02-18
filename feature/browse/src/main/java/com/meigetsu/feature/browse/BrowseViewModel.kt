package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.usecase.GlobalSearchUseCase
import com.meigetsu.core.network.JikanService
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.MediaSearchResult
import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import com.meigetsu.core.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BrowseUiEvent {
    data class ShowSnackbar(val message: String) : BrowseUiEvent()
}

data class BrowseFilters(
    val query: String = "",
    val genre: String? = null,
    val season: String? = null,
    val year: Int? = null,
    val format: String? = null,
    val status: String? = null,
    val sort: String? = "POPULARITY_DESC"
)

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val jikanService: JikanService,
    private val globalSearchUseCase: GlobalSearchUseCase,
    val extensionManager: ExtensionManager
) : ViewModel() {

    private val _filters = MutableStateFlow(BrowseFilters())
    val filters = _filters.asStateFlow()

    private val _animeResults = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val animeResults = _animeResults.asStateFlow()

    private val _mangaResults = MutableStateFlow<Resource<List<Manga>>>(Resource.Success(emptyList()))
    val mangaResults = _mangaResults.asStateFlow()

    private val _characterResults = MutableStateFlow<Resource<List<Character>>>(Resource.Success(emptyList()))
    val characterResults = _characterResults.asStateFlow()

    private val _extensionResults = MutableStateFlow<List<MediaSearchResult>>(emptyList())
    val extensionResults = _extensionResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _eventChannel = Channel<BrowseUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    private var currentPage = 1

    fun updateQuery(query: String) {
        _filters.value = _filters.value.copy(query = query)
        search()
    }

    fun updateGenre(genre: String?) {
        _filters.value = _filters.value.copy(genre = genre)
        search()
    }

    fun updateFilters(newFilters: BrowseFilters) {
        _filters.value = newFilters
        search()
    }

    fun search() {
        val currentFilters = _filters.value
        currentPage = 1

        viewModelScope.launch {
            _isSearching.value = true

            launch {
                mediaRepository.searchAnime(
                    currentFilters.query,
                    currentPage,
                    currentFilters.genre,
                    currentFilters.season,
                    currentFilters.year,
                    currentFilters.format,
                    currentFilters.status,
                    currentFilters.sort
                ).collect { _animeResults.value = it }
            }
            launch {
                mediaRepository.searchManga(
                    currentFilters.query,
                    currentPage,
                    currentFilters.genre,
                    currentFilters.format,
                    currentFilters.status,
                    currentFilters.sort
                ).collect { _mangaResults.value = it }
            }
            launch {
                if (currentFilters.query.isNotBlank()) {
                    mediaRepository.searchCharacters(currentFilters.query).collect { _characterResults.value = it }
                } else {
                    _characterResults.value = Resource.Success(emptyList())
                }
            }
            launch {
                if (currentFilters.query.isNotBlank()) {
                    _extensionResults.value = globalSearchUseCase.execute(currentFilters.query)
                } else {
                    _extensionResults.value = emptyList()
                }
            }

            _isSearching.value = false
        }
    }

    fun loadNextPage() {
        val currentFilters = _filters.value
        currentPage++

        viewModelScope.launch {
            mediaRepository.searchAnime(
                currentFilters.query,
                currentPage,
                currentFilters.genre,
                currentFilters.season,
                currentFilters.year,
                currentFilters.format,
                currentFilters.status,
                currentFilters.sort
            ).collect { resource ->
                if (resource is Resource.Success) {
                    val currentList = (_animeResults.value as? Resource.Success)?.data ?: emptyList()
                    _animeResults.value = Resource.Success(currentList + (resource.data ?: emptyList()))
                }
            }

            mediaRepository.searchManga(
                currentFilters.query,
                currentPage,
                currentFilters.genre,
                currentFilters.format,
                currentFilters.status,
                currentFilters.sort
            ).collect { resource ->
                if (resource is Resource.Success) {
                    val currentList = (_mangaResults.value as? Resource.Success)?.data ?: emptyList()
                    _mangaResults.value = Resource.Success(currentList + (resource.data ?: emptyList()))
                }
            }
        }
    }
}
