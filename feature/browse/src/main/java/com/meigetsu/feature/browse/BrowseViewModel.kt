package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.ExtensionRemote
import com.meigetsu.core.model.Anime
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
    val extensionManager: ExtensionManager
) : ViewModel() {

    private val _searchResult = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val searchResult: StateFlow<Resource<List<Anime>>> = _searchResult.asStateFlow()

    private val _characterSearchResult = MutableStateFlow<Resource<List<Character>>>(Resource.Success(emptyList()))
    val characterSearchResult: StateFlow<Resource<List<Character>>> = _characterSearchResult.asStateFlow()

    val availableExtensions: StateFlow<List<ExtensionRemote>> = extensionManager.availableExtensions

    private val _eventChannel = Channel<BrowseUiEvent>()
    val events = _eventChannel.receiveAsFlow()

    fun search(query: String, type: String = "ANIME") {
        if (type == "CHARACTER") {
            mediaRepository.searchCharacters(query).onEach {
                _characterSearchResult.value = it
            }.launchIn(viewModelScope)
        } else {
            mediaRepository.searchAnime(query, 1).onEach {
                _searchResult.value = it
            }.launchIn(viewModelScope)
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
