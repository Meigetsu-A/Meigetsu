package com.meigetsu.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.database.dao.ReadingStatsDao
import com.meigetsu.core.database.entity.ExtensionRepoEntity
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

    val themeMode: StateFlow<String> = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val cornerRadius: StateFlow<Int> = preferenceRepository.getCornerRadius()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    val incognitoMode: StateFlow<Boolean> = preferenceRepository.getIncognitoMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val adultContent: StateFlow<Boolean> = preferenceRepository.getAdultContent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val biometricEnabled: StateFlow<Boolean> = preferenceRepository.getBiometricEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val autoRefreshInterval: StateFlow<Int> = preferenceRepository.getAutoRefreshInterval()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val libraryLayout: StateFlow<String> = preferenceRepository.getLibraryLayout()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "GRID")

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

    fun setAdultContent(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setAdultContent(enabled) }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch { preferenceRepository.setBiometricEnabled(enabled) }
    }

    fun setAutoRefreshInterval(minutes: Int) {
        viewModelScope.launch { preferenceRepository.setAutoRefreshInterval(minutes) }
    }

    fun setLibraryLayout(layout: String) {
        viewModelScope.launch { preferenceRepository.setLibraryLayout(layout) }
    }

    val repositories: StateFlow<List<ExtensionRepoEntity>> = extensionManager.getRepositories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRepository(url: String, name: String) {
        extensionManager.addRepository(url, name)
    }

    fun removeRepository(url: String) {
        extensionManager.removeRepository(url)
    }

    val stats: StateFlow<List<ReadingStatsEntity>> = statsDao.getAllStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
