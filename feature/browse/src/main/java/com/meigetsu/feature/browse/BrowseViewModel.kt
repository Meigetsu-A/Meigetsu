package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val aniListService: AniListService
) : ViewModel() {

    private val _selectedGenre = MutableStateFlow("Action")
    val selectedGenre = _selectedGenre.asStateFlow()

    private val _spotlight = MutableStateFlow<Media?>(null)
    val spotlight = _spotlight.asStateFlow()

    private val _results = MutableStateFlow<List<Media>>(emptyList())
    val results = _results.asStateFlow()

    init {
        loadGenreData("Action")
    }

    fun onGenreSelected(genre: String) {
        _selectedGenre.value = genre
        loadGenreData(genre)
    }

    private fun loadGenreData(genre: String) {
        viewModelScope.launch {
            val list = aniListService.getTrending(MediaType.ANIME)
            _results.value = list
            _spotlight.value = list.firstOrNull()
        }
    }
}
