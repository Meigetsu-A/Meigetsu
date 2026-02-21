package com.meigetsu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aniListService: AniListService,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _featuredMedia = MutableStateFlow<Media?>(null)
    val featuredMedia = _featuredMedia.asStateFlow()

    private val _trendingMedia = MutableStateFlow<List<Media>>(emptyList())
    val trendingMedia = _trendingMedia.asStateFlow()

    private val _continueWatching = MutableStateFlow<List<Media>>(emptyList())
    val continueWatching = _continueWatching.asStateFlow()

    private val _continueReading = MutableStateFlow<List<Media>>(emptyList())
    val continueReading = _continueReading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Trending
            _trendingMedia.value = aniListService.getTrending(MediaType.ANIME)

            // Continue Watching/Reading from Library and History
            // For now, just take from Library
            libraryRepository.getLibrary().collect { library ->
                _continueWatching.value = library.filter { it.type == MediaType.ANIME }
                _continueReading.value = library.filter { it.type == MediaType.MANGA || it.type == MediaType.NOVEL }
                _featuredMedia.value = _continueWatching.value.firstOrNull() ?: _trendingMedia.value.firstOrNull()
            }
        }
    }
}
