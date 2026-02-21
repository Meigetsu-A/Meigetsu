package com.meigetsu.feature.details
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.data.repository.LibraryRepository
import com.meigetsu.core.data.repository.SourceRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val aniListService: AniListService,
    private val sourceRepository: SourceRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    private val mediaId: String = checkNotNull(savedStateHandle["id"])
    private val mediaType: String = checkNotNull(savedStateHandle["type"])
    private val _media = MutableStateFlow<Media?>(null)
    val media = _media.asStateFlow()
    private val _sources = MutableStateFlow<List<Source>>(emptyList())
    val sources = _sources.asStateFlow()
    private val _selectedSource = MutableStateFlow<Source?>(null)
    val selectedSource = _selectedSource.asStateFlow()
    private val _episodes = MutableStateFlow<List<SourceEpisode>>(emptyList())
    val episodes = _episodes.asStateFlow()
    private val _chapters = MutableStateFlow<List<SourceChapter>>(emptyList())
    val chapters = _chapters.asStateFlow()
    private val _isBookmarked = MutableStateFlow(false)
    val isBookmarked = _isBookmarked.asStateFlow()
    private val _sourceMediaId = MutableStateFlow<String?>(null)
    val sourceMediaId = _sourceMediaId.asStateFlow()

    init {
        loadDetails()
    }
    private fun loadDetails() {
        viewModelScope.launch {
            val type = MediaType.valueOf(mediaType)
            val details = aniListService.getDetails(mediaId, type)
            _media.value = details
            val availableSources = sourceRepository.getSources(type)
            _sources.value = availableSources
            if (availableSources.isNotEmpty()) {
                selectSource(availableSources.first())
            }
            libraryRepository.getLibrary().collect { library ->
                _isBookmarked.value = library.any { it.id == mediaId }
            }
        }
    }
    fun selectSource(source: Source) {
        _selectedSource.value = source
        viewModelScope.launch {
            val currentMedia = _media.value ?: return@launch
            // Search source by title to find correct ID
            val results = source.search(currentMedia.title.sanitize())
            val matched = results.firstOrNull() // Simple heuristic: first result
            if (matched != null) {
                _sourceMediaId.value = matched.id
                if (currentMedia.type == MediaType.ANIME) {
                    _episodes.value = source.getEpisodes(matched.id)
                } else {
                    _chapters.value = source.getChapters(matched.id)
                }
            } else {
                _episodes.value = emptyList()
                _chapters.value = emptyList()
                _sourceMediaId.value = null
            }
        }
    }
    fun toggleBookmark() {
        viewModelScope.launch {
            val m = _media.value ?: return@launch
            if (_isBookmarked.value) {
                libraryRepository.removeFromLibrary(m.id)
            } else {
                libraryRepository.addToLibrary(m)
            }
        }
    }
    private fun String.sanitize(): String = this.replace(Regex("[^a-zA-Z0-9 ]"), "")
}
