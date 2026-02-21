package com.meigetsu.feature.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val aniListService: AniListService,
    private val libraryRepository: LibraryRepository
) : ViewModel() {
    private val _featuredMedia = MutableStateFlow<Media?>(null)
    val featuredMedia = _featuredMedia.asStateFlow()
    private val _trendingAnime = MutableStateFlow<List<Media>>(emptyList())
    val trendingAnime = _trendingAnime.asStateFlow()
    private val _popularManga = MutableStateFlow<List<Media>>(emptyList())
    val popularManga = _popularManga.asStateFlow()
    private val _upcomingAnime = MutableStateFlow<List<Media>>(emptyList())
    val upcomingAnime = _upcomingAnime.asStateFlow()
    private val _continueWatching = MutableStateFlow<List<Media>>(emptyList())
    val continueWatching = _continueWatching.asStateFlow()
    private val _continueReading = MutableStateFlow<List<Media>>(emptyList())
    val continueReading = _continueReading.asStateFlow()
    init {
        loadData()
    }
    private fun loadData() {
        viewModelScope.launch {
            _trendingAnime.value = aniListService.getTrending(MediaType.ANIME)
            _popularManga.value = aniListService.getPopular(MediaType.MANGA)
            _upcomingAnime.value = aniListService.getUpcoming(MediaType.ANIME)
            libraryRepository.getLibrary().collect { library ->
                _continueWatching.value = library.filter { it.type == MediaType.ANIME }
                _continueReading.value = library.filter { it.type == MediaType.MANGA || it.type == MediaType.NOVEL }
                _featuredMedia.value = _continueWatching.value.firstOrNull() ?: _trendingAnime.value.firstOrNull()
            }
        }
    }
}
