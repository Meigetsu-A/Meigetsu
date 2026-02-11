package com.meigetsu.feature.reader

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chapterId: String = checkNotNull(savedStateHandle["chapterId"])

    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages: StateFlow<List<String>> = _pages.asStateFlow()

    fun loadPages(urls: List<String>) {
        _pages.value = urls
    }
}
