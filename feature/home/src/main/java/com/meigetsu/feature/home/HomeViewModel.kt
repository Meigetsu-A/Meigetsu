package com.meigetsu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private val _trendingAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Loading())
    val trendingAnime: StateFlow<Resource<List<Anime>>> = _trendingAnime.asStateFlow()

    private val _continueWatching = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val continueWatching: StateFlow<Resource<List<Anime>>> = _continueWatching.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadTrending()
        loadContinueWatching()
    }

    private fun loadTrending() {
        mediaRepository.getTrendingAnime().onEach {
            _trendingAnime.value = it
        }.launchIn(viewModelScope)
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
}
