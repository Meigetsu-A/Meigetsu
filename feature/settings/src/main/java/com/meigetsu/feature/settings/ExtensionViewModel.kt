package com.meigetsu.feature.settings

import androidx.lifecycle.ViewModel
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    val extensionManager: ExtensionManager
) : ViewModel()
