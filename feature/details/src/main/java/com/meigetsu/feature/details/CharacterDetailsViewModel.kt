package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val charId: String = checkNotNull(savedStateHandle["charId"])

    private val _uiState = MutableStateFlow<Resource<Character>>(Resource.Loading())
    val uiState: StateFlow<Resource<Character>> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    init {
        loadDetails()
        checkIfFavorite()
    }

    private fun loadDetails() {
        mediaRepository.getCharacterDetails(charId).onEach {
            _uiState.value = it
        }.launchIn(viewModelScope)
    }

    private fun checkIfFavorite() {
        libraryRepository.getFavoriteCharacters().onEach { chars ->
            _isFavorite.value = chars.any { it.id == charId }
        }.launchIn(viewModelScope)
    }

    fun toggleFavorite() {
        val char = (_uiState.value as? Resource.Success)?.data ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                libraryRepository.removeCharacterFromLibrary(charId)
            } else {
                libraryRepository.addCharacterToLibrary(char)
            }
        }
    }
}
