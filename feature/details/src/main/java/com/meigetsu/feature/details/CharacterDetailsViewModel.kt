package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val charId: String = checkNotNull(savedStateHandle["charId"])

    private val _uiState = MutableStateFlow<Resource<Character>>(Resource.Loading())
    val uiState: StateFlow<Resource<Character>> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        mediaRepository.getCharacterDetails(charId).onEach {
            _uiState.value = it
        }.launchIn(viewModelScope)
    }
}
