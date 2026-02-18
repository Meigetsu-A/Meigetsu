package com.meigetsu.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.database.dao.ReadingStatsDao
import com.meigetsu.core.database.entity.ReadingStatsEntity
import com.meigetsu.core.domain.repository.PreferenceRepository
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    private val libraryRepository: LibraryRepository,
    private val extensionManager: ExtensionManager,
    private val statsDao: ReadingStatsDao
) : ViewModel() {

    val primaryColor: StateFlow<Color> = preferenceRepository.getPrimaryColor()
        .map { Color(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFFE50914))

    val secondaryColor: StateFlow<Color> = preferenceRepository.getSecondaryColor()
        .map { Color(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFF141414))

    val accentColor: StateFlow<Color> = preferenceRepository.getAccentColor()
        .map { Color(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFFB9090B))

    val themeMode: StateFlow<String> = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val cornerRadius: StateFlow<Int> = preferenceRepository.getCornerRadius()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 12)

    val incognitoMode: StateFlow<Boolean> = preferenceRepository.getIncognitoMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = preferenceRepository.getBiometricEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val dataSaverEnabled: StateFlow<Boolean> = preferenceRepository.isDataSaverEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val playbackSpeed: StateFlow<Float> = preferenceRepository.getDefaultPlaybackSpeed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val defaultQuality: StateFlow<String> = preferenceRepository.getDefaultQuality()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "1080p")

    val autoNextEnabled: StateFlow<Boolean> = preferenceRepository.isAutoNextEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val skipIntroAutoEnabled: StateFlow<Boolean> = preferenceRepository.isSkipIntroAutoEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val readingMode: StateFlow<String> = preferenceRepository.getReadingMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "VERTICAL")

    val preloadPageCount: StateFlow<Int> = preferenceRepository.getPreloadPageCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5)

    fun updatePrimaryColor(color: Color) {
        viewModelScope.launch { preferenceRepository.setPrimaryColor(color.toArgb()) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferenceRepository.setThemeMode(mode) }
    }

    fun updateCornerRadius(radius: Int) {
        viewModelScope.launch { preferenceRepository.setCornerRadius(radius) }
    }

    fun setIncognito(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setIncognitoMode(enabled) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setBiometricEnabled(enabled) }
    }

    fun setDataSaverEnabled(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setDataSaverEnabled(enabled) }
    }

    fun setPlaybackSpeed(speed: Float) {
        viewModelScope.launch { preferenceRepository.setDefaultPlaybackSpeed(speed) }
    }

    fun setDefaultQuality(quality: String) {
        viewModelScope.launch { preferenceRepository.setDefaultQuality(quality) }
    }

    fun setAutoNext(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setAutoNextEnabled(enabled) }
    }

    fun setSkipIntroAuto(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setSkipIntroAutoEnabled(enabled) }
    }

    fun setReadingMode(mode: String) {
        viewModelScope.launch { preferenceRepository.setReadingMode(mode) }
    }

    fun setPreloadPageCount(count: Int) {
        viewModelScope.launch { preferenceRepository.setPreloadPageCount(count) }
    }

    val stats: StateFlow<List<ReadingStatsEntity>> = statsDao.getAllStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
