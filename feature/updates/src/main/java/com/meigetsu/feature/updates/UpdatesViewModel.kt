package com.meigetsu.feature.updates

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor() : ViewModel() {
    private val _updates = MutableStateFlow<List<UpdateItem>>(emptyList())
    val updates: StateFlow<List<UpdateItem>> = _updates.asStateFlow()
}

data class UpdateItem(
    val id: String,
    val title: String,
    val updateInfo: String,
    val timestamp: Long
)
