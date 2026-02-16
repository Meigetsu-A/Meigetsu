package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.domain.usecase.GlobalSearchUseCase
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.ExtensionRemote
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

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val globalSearchUseCase: GlobalSearchUseCase,
    val extensionManager: ExtensionManager
) : ViewModel() {

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

    val availableExtensions: StateFlow<List<ExtensionRemote>> = extensionManager.availableExtensions

    private val _eventChannel = Channel<BrowseUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isSearching.value = true

            // Launch searches concurrently
            launch {
                mediaRepository.searchAnime(query, 1).collect { _animeResults.value = it }
            }
            launch {
                mediaRepository.searchManga(query, 1).collect { _mangaResults.value = it }
            }
            launch {
                mediaRepository.searchCharacters(query).collect { _characterResults.value = it }
            }
            launch {
                _extensionResults.value = globalSearchUseCase.execute(query)
            }

            _isSearching.value = false
        }
    }

    fun installExtension(remote: ExtensionRemote) {
        viewModelScope.launch {
            _eventChannel.send(BrowseUiEvent.ShowSnackbar("Installing ${remote.name}..."))
            extensionManager.installExtension(remote)
            _eventChannel.send(BrowseUiEvent.ShowSnackbar("Installed ${remote.name}"))
        }
    }
}
