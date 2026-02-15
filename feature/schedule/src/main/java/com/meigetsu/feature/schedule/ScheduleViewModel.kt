package com.meigetsu.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.network.JikanAnimeData
import com.meigetsu.core.network.JikanService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val jikanService: JikanService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val days = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")

    init {
        loadSchedule("monday")
    }

    fun loadSchedule(day: String) {
        viewModelScope.launch {
            _uiState.value = ScheduleUiState.Loading
            try {
                val response = jikanService.getSchedules(day)
                _uiState.value = ScheduleUiState.Success(response.data)
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

sealed class ScheduleUiState {
    object Loading : ScheduleUiState()
    data class Success(val anime: List<JikanAnimeData>) : ScheduleUiState()
    data class Error(val message: String) : ScheduleUiState()
}
