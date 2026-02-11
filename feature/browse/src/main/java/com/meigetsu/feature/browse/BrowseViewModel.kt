package com.meigetsu.feature.browse

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
class BrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : ViewModel() {

    private val _searchResult = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val searchResult: StateFlow<Resource<List<Anime>>> = _searchResult.asStateFlow()

    fun search(query: String) {
        mediaRepository.searchAnime(query, 1).onEach {
            _searchResult.value = it
        }.launchIn(viewModelScope)
    }
}
