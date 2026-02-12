package com.meigetsu.feature.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.usecase.GlobalSearchUseCase
import com.meigetsu.core.extensions.MediaSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalSearchViewModel @Inject constructor(
    private val globalSearchUseCase: GlobalSearchUseCase
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<MediaSearchResult>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _searchResults.value = globalSearchUseCase.execute(query)
            _isLoading.value = false
        }
    }
}
