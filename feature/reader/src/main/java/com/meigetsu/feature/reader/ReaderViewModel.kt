package com.meigetsu.feature.reader
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.data.repository.SourceRepository
import com.meigetsu.core.model.SourceChapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sourceRepository: SourceRepository
) : ViewModel() {
    private val mediaId: String = checkNotNull(savedStateHandle["id"])
    private val sourceName: String = checkNotNull(savedStateHandle["source"])
    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages = _pages.asStateFlow()
    private val _chapters = MutableStateFlow<List<SourceChapter>>(emptyList())
    val chapters = _chapters.asStateFlow()
    init {
        loadMedia()
    }
    private fun loadMedia() {
        viewModelScope.launch {
            val source = sourceRepository.getSource(sourceName) ?: return@launch
            val chaps = source.getChapters(mediaId)
            _chapters.value = chaps
            if (chaps.isNotEmpty()) {
                _pages.value = source.getPages(chaps.first())
            }
        }
    }
    fun selectChapter(chapter: SourceChapter) {
        viewModelScope.launch {
            val source = sourceRepository.getSource(sourceName) ?: return@launch
            _pages.value = source.getPages(chapter)
        }
    }
}
