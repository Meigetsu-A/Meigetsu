package com.meigetsu.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.model.*
import com.meigetsu.core.network.AniListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val aniListService: AniListService
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Media>>(emptyList())
    val results = _results.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        if (newQuery.length >= 3) {
            viewModelScope.launch {
                _results.value = aniListService.search(newQuery, MediaType.ANIME)
            }
        }
    }

    fun removeRecentSearch(search: String) {
        _recentSearches.value = _recentSearches.value - search
    }
}
