package com.meigetsu.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.PreferenceRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    val primaryColor: StateFlow<Color> = preferenceRepository.getPrimaryColor()
        .map { Color(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFF6650a4))

    val cornerRadius: StateFlow<Int> = preferenceRepository.getCornerRadius()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    val incognitoMode: StateFlow<Boolean> = preferenceRepository.getIncognitoMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val adultContent: StateFlow<Boolean> = preferenceRepository.getAdultContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val watchTime: StateFlow<String> = flow { emit("124h 32m") }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0h 0m")
    val readCount: StateFlow<Int> = flow { emit(1458) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updatePrimaryColor(color: Color) {
        viewModelScope.launch { preferenceRepository.setPrimaryColor(color.toArgb()) }
    }

    fun updateCornerRadius(radius: Int) {
        viewModelScope.launch { preferenceRepository.setCornerRadius(radius) }
    }

    fun setIncognito(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setIncognitoMode(enabled) }
    }

    fun setAdultContent(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setAdultContent(enabled) }
    }
}
