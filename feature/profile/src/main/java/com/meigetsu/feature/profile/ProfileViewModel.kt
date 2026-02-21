package com.meigetsu.feature.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    val primaryColor = preferenceRepository.getPrimaryColor()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF00BFFF)
    val themeMode = preferenceRepository.getThemeMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")
    val cornerRadius = preferenceRepository.getCornerRadius()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 16)
    fun setPrimaryColor(color: Long) {
        viewModelScope.launch { preferenceRepository.setPrimaryColor(color) }
    }
    fun setThemeMode(mode: String) {
        viewModelScope.launch { preferenceRepository.setThemeMode(mode) }
    }
    fun setCornerRadius(radius: Int) {
        viewModelScope.launch { preferenceRepository.setCornerRadius(radius) }
    }
}
