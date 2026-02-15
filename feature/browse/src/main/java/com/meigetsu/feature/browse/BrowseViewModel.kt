package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.network.JikanService
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
    private val jikanService: JikanService,
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
            viewModelScope.launch {
                _characterSearchResult.value = Resource.Loading()
                try {
                    val response = jikanService.searchCharacters(query)
                    val characters = response.data.map {
                        Character(
                            id = it.mal_id.toString(),
                            name = it.name,
                            image = it.images.jpg.image_url,
                            description = it.about
                        )
                    }
                    _characterSearchResult.value = Resource.Success(characters)
                } catch (e: Exception) {
                    _characterSearchResult.value = Resource.Error(e.message ?: "Search failed")
                }
            }
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
