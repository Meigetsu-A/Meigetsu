package com.meigetsu.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val libraryItems: StateFlow<List<Media>> = libraryRepository.getLibrary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
