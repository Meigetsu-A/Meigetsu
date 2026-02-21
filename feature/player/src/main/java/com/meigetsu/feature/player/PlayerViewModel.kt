package com.meigetsu.feature.player

import androidx.lifecycle.ViewModel
import com.meigetsu.core.data.repository.SourceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val sourceRepository: SourceRepository
) : ViewModel() {
    // Logic for loading stream URL
}
