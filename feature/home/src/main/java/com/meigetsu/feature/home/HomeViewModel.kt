package com.meigetsu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.model.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _trendingAnime = MutableStateFlow<Resource<List<Anime>>>(Resource.Loading())
    val trendingAnime: StateFlow<Resource<List<Anime>>> = _trendingAnime.asStateFlow()

    init {
        loadTrending()
    }

    private fun loadTrending() {
        mediaRepository.getTrendingAnime().onEach {
            _trendingAnime.value = it
        }.launchIn(viewModelScope)
    }
}
