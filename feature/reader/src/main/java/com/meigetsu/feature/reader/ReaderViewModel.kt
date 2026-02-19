package com.meigetsu.feature.reader

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import coil.request.ImageRequest
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.model.Chapter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.meigetsu.core.domain.repository.LibraryRepository

@HiltViewModel
class ReaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val libraryRepository: LibraryRepository,
    private val extensionManager: ExtensionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val chapterId: String = savedStateHandle["chapterId"] ?: ""
    private val mangaId: String = savedStateHandle["mangaId"] ?: ""
    private val chapterUrl: String? = savedStateHandle.get<String>("url")?.let {
        java.net.URLDecoder.decode(it, java.nio.charset.StandardCharsets.UTF_8.toString())
    }

    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages: StateFlow<List<String>> = _pages.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage = _currentPage.asStateFlow()

    init {
        fetchPages()
    }

    private fun fetchPages() {
        viewModelScope.launch {
            val providers = extensionManager.mangaProviders.value.values
            val provider = providers.find { it.metadata.id != "anilist" }
                ?: providers.firstOrNull() ?: return@launch

            val chapter = Chapter(chapterId, mangaId, 0.0, null, null, chapterUrl ?: "")
            val urls = provider.getPages(chapter)
            _pages.value = urls
            preloadPages(0, 3)
        }
    }

    fun onPageChanged(page: Int) {
        _currentPage.value = page
        preloadPages(page + 1, page + 3)

        viewModelScope.launch {
            if (mangaId.isNotBlank()) {
                libraryRepository.updateReadHistory(mangaId, 0.0, page)
            }
        }
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
