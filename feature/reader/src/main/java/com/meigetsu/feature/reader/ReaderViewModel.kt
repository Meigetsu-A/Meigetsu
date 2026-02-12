package com.meigetsu.feature.reader

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chapterId: String = savedStateHandle["chapterId"] ?: ""

    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages: StateFlow<List<String>> = _pages.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    fun loadPages(urls: List<String>) {
        _pages.value = urls
        preloadPages(0, 3)
    }

    fun onPageChanged(page: Int) {
        _currentPage.value = page
        preloadPages(page + 1, page + 3)
    }

    private fun preloadPages(start: Int, end: Int) {
        val urls = _pages.value
        for (i in maxOf(0, start)..minOf(urls.size - 1, end)) {
            val request = ImageRequest.Builder(context)
                .data(urls[i])
                .build()
            context.imageLoader.enqueue(request)
        }
    }
}
