package com.meigetsu.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.util.sanitizeTitle
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.data.repository.SourceRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailViewModel @Inject constructor(
    private val aniListService: AniListService,
    private val sourceRepository: SourceRepository,
    private val libraryRepository: LibraryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = savedStateHandle["id"] ?: ""
    private val mediaType: MediaType = MediaType.valueOf(savedStateHandle["type"] ?: "ANIME")

    private val _media = MutableStateFlow<Media?>(null)
    val media = _media.asStateFlow()

    private val _episodes = MutableStateFlow<List<SourceEpisode>>(emptyList())
    val episodes = _episodes.asStateFlow()

    private val _chapters = MutableStateFlow<List<SourceChapter>>(emptyList())
    val chapters = _chapters.asStateFlow()

    val sources = sourceRepository.getSources(mediaType)
    private val _selectedSource = MutableStateFlow(sources.firstOrNull())
    val selectedSource = _selectedSource.asStateFlow()

    init {
        loadDetails()
    }

    fun selectSource(source: Source) {
        _selectedSource.value = source
        loadSourceContent()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            val m = aniListService.getDetails(mediaId, mediaType)
            _media.value = m
            loadSourceContent()
        }
    }

    private fun loadSourceContent() {
        viewModelScope.launch {
            val title = _media.value?.title?.sanitizeTitle() ?: return@launch
            val source = _selectedSource.value ?: return@launch

            val searchResults = source.search(title)
            val bestMatch = searchResults.firstOrNull()

            bestMatch?.let {
                if (source.type == MediaType.ANIME) {
                    _episodes.value = source.getEpisodes(it.id)
                } else {
                    _chapters.value = source.getChapters(it.id)
                }
            }
        }
    }

    fun toggleLibrary() {
        viewModelScope.launch {
            _media.value?.let { libraryRepository.addToLibrary(it) }
        }
    }
}
