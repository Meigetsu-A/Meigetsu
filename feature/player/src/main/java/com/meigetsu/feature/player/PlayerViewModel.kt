package com.meigetsu.feature.player
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.data.repository.SourceRepository
import com.meigetsu.core.model.SourceEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class PlayerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sourceRepository: SourceRepository
) : ViewModel() {
    private val mediaId: String = checkNotNull(savedStateHandle["id"])
    private val sourceName: String = checkNotNull(savedStateHandle["source"])
    private val _streamUrl = MutableStateFlow<String?>(null)
    val streamUrl = _streamUrl.asStateFlow()
    private val _episodes = MutableStateFlow<List<SourceEpisode>>(emptyList())
    val episodes = _episodes.asStateFlow()
    init {
        loadMedia()
    }
    private fun loadMedia() {
        viewModelScope.launch {
            val source = sourceRepository.getSource(sourceName) ?: return@launch
            // Search for the media first to get the source-specific ID
            // Since we don't have a mapping, we search by ID (which we assume is title for now, but better to fix mapping later)
            // For now, assume mediaId passed is actually the source ID from DetailScreen
            val eps = source.getEpisodes(mediaId)
            _episodes.value = eps
            if (eps.isNotEmpty()) {
                _streamUrl.value = source.getStreamUrl(eps.first())
            }
        }
    }
    fun selectEpisode(episode: SourceEpisode) {
        viewModelScope.launch {
            val source = sourceRepository.getSource(sourceName) ?: return@launch
            _streamUrl.value = source.getStreamUrl(episode)
        }
    }
}
