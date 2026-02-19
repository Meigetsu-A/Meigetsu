package com.meigetsu.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.extensions.Extension
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    private val extensionManager: ExtensionManager
) : ViewModel() {

    val installedExtensions: StateFlow<List<Extension>> = extensionManager.installedExtensions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disabledExtensionIds: StateFlow<Set<String>> = extensionManager.disabledExtensionIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun toggleExtension(id: String) {
        extensionManager.toggleExtension(id)
    }

    fun addExtensionRepo(url: String) {
        // Future: Logic to fetch and install extensions from a repo URL
    }
}
