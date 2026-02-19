package com.meigetsu.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.DownloadRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    val libraryAnime: StateFlow<List<Anime>> = libraryRepository.getLibraryAnime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val libraryManga: StateFlow<List<com.meigetsu.core.model.Manga>> = libraryRepository.getLibraryManga()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteCharacters: StateFlow<List<Character>> = libraryRepository.getFavoriteCharacters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<Download>> = downloadRepository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = libraryRepository.getCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()

    fun toggleSelection(id: String) {
        _selectedIds.value = if (_selectedIds.value.contains(id)) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelected() {
        viewModelScope.launch {
            _selectedIds.value.forEach { libraryRepository.removeFromLibrary(it) }
            clearSelection()
        }
    }

    fun updateSelectedStatus(status: String) {
        viewModelScope.launch {
            libraryRepository.updateItemsStatus(_selectedIds.value.toList(), status)
            clearSelection()
        }
    }

    fun updateSelectedCategory(categoryId: String?) {
        viewModelScope.launch {
            libraryRepository.updateItemsCategory(_selectedIds.value.toList(), categoryId)
            clearSelection()
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            libraryRepository.addCategory(name)
        }
    }
}
