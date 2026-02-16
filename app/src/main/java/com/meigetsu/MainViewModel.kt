package com.meigetsu

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {

    val primaryColor: StateFlow<Color> = preferenceRepository.getPrimaryColor()
        .map { Color(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Color(0xFFE50914))

    val themeMode: StateFlow<String> = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    val cornerRadius: StateFlow<Int> = preferenceRepository.getCornerRadius()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    val biometricEnabled: StateFlow<Boolean> = preferenceRepository.getBiometricEnabled()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isOnboardingCompleted: StateFlow<Boolean> = preferenceRepository.isOnboardingCompleted()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true) // Default to true to avoid flicker

    private val _isPlayerActive = MutableStateFlow(false)
    val isPlayerActive: StateFlow<Boolean> = _isPlayerActive.asStateFlow()

    fun setPlayerActive(active: Boolean) {
        _isPlayerActive.value = active
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferenceRepository.setOnboardingCompleted(true)
        }
    }
}
