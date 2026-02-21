package com.meigetsu.feature.reader

import androidx.lifecycle.ViewModel
import com.meigetsu.core.data.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {
    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages = _pages.asStateFlow()

    // Logic for loading pages
}
