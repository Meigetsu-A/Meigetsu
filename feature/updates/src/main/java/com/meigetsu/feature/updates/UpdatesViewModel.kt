package com.meigetsu.feature.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor() : ViewModel() {
    private val _updates = MutableStateFlow<List<UpdateItem>>(emptyList())
    val updates: StateFlow<List<UpdateItem>> = _updates.asStateFlow()

    init {
        loadUpdates()
    }

    private fun loadUpdates() {
        viewModelScope.launch {
            // Simulated fetch of recent activity
            _updates.value = listOf(
                UpdateItem("1", "One Piece", "Episode 1100 released", System.currentTimeMillis()),
                UpdateItem("2", "Solo Leveling", "Chapter 180 updated", System.currentTimeMillis() - 3600000)
            )
        }
    }
}

data class UpdateItem(
    val id: String,
    val title: String,
    val updateInfo: String,
    val timestamp: Long
)
