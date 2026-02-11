package com.meigetsu.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.Anime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val libraryAnime: StateFlow<List<Anime>> = libraryRepository.getLibraryAnime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
