package com.meigetsu.feature.settings

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _primaryColor = MutableStateFlow(Color(0xFF6650a4))
    val primaryColor: StateFlow<Color> = _primaryColor.asStateFlow()

    fun updatePrimaryColor(color: Color) {
        _primaryColor.value = color
    }
}
