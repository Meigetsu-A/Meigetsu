package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.ExtensionRemote
import com.meigetsu.core.model.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    val extensionManager: ExtensionManager
) : ViewModel() {

    private val _searchResult = MutableStateFlow<Resource<List<Anime>>>(Resource.Success(emptyList()))
    val searchResult: StateFlow<Resource<List<Anime>>> = _searchResult.asStateFlow()

    val availableExtensions: StateFlow<List<ExtensionRemote>> = extensionManager.availableExtensions

    fun search(query: String) {
        mediaRepository.searchAnime(query, 1).onEach {
            _searchResult.value = it
        }.launchIn(viewModelScope)
    }

    fun installExtension(remote: ExtensionRemote) {
        viewModelScope.launch {
            extensionManager.installExtension(remote)
        }
    }
}
