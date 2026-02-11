package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
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
class DetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = checkNotNull(savedStateHandle["mediaId"])

    private val _uiState = MutableStateFlow<Resource<Anime>>(Resource.Loading())
    val uiState: StateFlow<Resource<Anime>> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        mediaRepository.getAnimeDetails(mediaId).onEach {
            _uiState.value = it
        }.launchIn(viewModelScope)
    }
}
