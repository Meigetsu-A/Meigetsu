package com.meigetsu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.repository.EverythingMoeRepository
import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import com.meigetsu.core.model.ExternalSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val libraryRepository: LibraryRepository,
    private val everythingMoeRepository: EverythingMoeRepository
) : ViewModel() {

    private val _trendingAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Loading())
    val trendingAnime: StateFlow<Resource<List<Anime>>> = _trendingAnime.asStateFlow()

    private val _popularAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Loading())
    val popularAnime: StateFlow<Resource<List<Anime>>> = _popularAnime.asStateFlow()

    private val _trendingManga = MutableStateFlow<Resource<List<Manga>>>(Resource.Loading())
    val trendingManga: StateFlow<Resource<List<Manga>>> = _trendingManga.asStateFlow()

    private val _popularManga = MutableStateFlow<Resource<List<Manga>>>(Resource.Loading())
    val popularManga: StateFlow<Resource<List<Manga>>> = _popularManga.asStateFlow()

    private val _recommendedAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Loading())
    val recommendedAnime: StateFlow<Resource<List<Anime>>> = _recommendedAnime.asStateFlow()

    private val _continueWatching = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val continueWatching: StateFlow<Resource<List<Anime>>> = _continueWatching.asStateFlow()

    private val _topSources = MutableStateFlow<Resource<List<ExternalSource>>>(Resource.Loading())
    val topSources: StateFlow<Resource<List<ExternalSource>>> = _topSources.asStateFlow()

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _searchResultsAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val searchResultsAnime = _searchResultsAnime.asStateFlow()

    private val _searchResultsManga = MutableStateFlow<Resource<List<Manga>>>(Resource.Success(emptyList()))
    val searchResultsManga = _searchResultsManga.asStateFlow()

    val genres = listOf(
        "Action", "Adventure", "Comedy", "Drama", "Fantasy",
        "Horror", "Mahou Shoujo", "Mecha", "Music", "Mystery",
        "Psychological", "Romance", "Sci-Fi", "Slice of Life",
        "Sports", "Supernatural", "Thriller"
    )

    init {
        refresh()

        searchQuery
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                if (query.isNotBlank()) performSearch(query)
                else {
                    _isSearching.value = false
                    _searchResultsAnime.value = Resource.Success(emptyList())
                    _searchResultsManga.value = Resource.Success(emptyList())
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateGenre(genre: String?) {
        _selectedGenre.value = if (_selectedGenre.value == genre) null else genre
        if (_selectedGenre.value != null) {
            if (_searchQuery.value.isNotBlank()) {
                performSearch(_searchQuery.value)
            } else {
                performGenreSearch(_selectedGenre.value!!)
            }
        } else if (_searchQuery.value.isBlank()) {
            _isSearching.value = false
        } else {
            performSearch(_searchQuery.value)
        }
    }

    private fun performSearch(query: String) {
        _isSearching.value = true
        mediaRepository.searchAnime(query, 1, _selectedGenre.value).onEach {
            _searchResultsAnime.value = it
        }.launchIn(viewModelScope)

        mediaRepository.searchManga(query, 1, _selectedGenre.value).onEach {
            _searchResultsManga.value = it
        }.launchIn(viewModelScope)
    }

    private fun performGenreSearch(genre: String) {
        _isSearching.value = true
        mediaRepository.searchAnime(null, 1, genre).onEach {
            _searchResultsAnime.value = it
        }.launchIn(viewModelScope)

        mediaRepository.searchManga(null, 1, genre).onEach {
            _searchResultsManga.value = it
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        loadTrending()
        loadPopular()
        loadRecommended()
        loadContinueWatching()
        loadSources()
    }

    private fun loadTrending() {
        mediaRepository.getTrendingAnime().onEach {
            _trendingAnime.value = it
        }.launchIn(viewModelScope)

        mediaRepository.getTrendingManga().onEach {
            _trendingManga.value = it
        }.launchIn(viewModelScope)
    }

    private fun loadPopular() {
        mediaRepository.getPopularAnime().onEach {
            _popularAnime.value = it
        }.launchIn(viewModelScope)

        mediaRepository.getPopularManga().onEach {
            _popularManga.value = it
        }.launchIn(viewModelScope)
    }

    private fun loadRecommended() {
        viewModelScope.launch {
            // Get genres from history
            libraryRepository.getWatchHistory().take(1).collect { ids ->
                if (ids.isNotEmpty()) {
                    mediaRepository.getMultipleAnime(ids).collect { resource ->
                        if (resource is Resource.Success) {
                            val genres = resource.data?.flatMap { it.genres }?.groupBy { it }
                                ?.mapValues { it.value.size }?.toList()?.sortedByDescending { it.second }
                                ?.map { it.first } ?: emptyList()

                            mediaRepository.getRecommendedAnime(genres).collect {
                                _recommendedAnime.value = it
                            }
                        }
                    }
                } else {
                    mediaRepository.getRecommendedAnime(null).collect {
                        _recommendedAnime.value = it
                    }
                }
            }
        }
    }

    private fun loadContinueWatching() {
        viewModelScope.launch {
            libraryRepository.getWatchHistory().take(1).collect { ids ->
                if (ids.isNotEmpty()) {
                    mediaRepository.getMultipleAnime(ids).collect {
                        _continueWatching.value = it
                    }
                }
            }
        }
    }

    private fun loadSources() {
        everythingMoeRepository.getSources().onEach {
            _topSources.value = it
        }.launchIn(viewModelScope)
    }
}
