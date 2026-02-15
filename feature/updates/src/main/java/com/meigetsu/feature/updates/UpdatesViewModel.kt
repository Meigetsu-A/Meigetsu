package com.meigetsu.feature.updates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.domain.repository.LibraryRepository
import com.meigetsu.core.domain.repository.PreferenceRepository
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatesViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val extensionManager: ExtensionManager,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    private val _updates = MutableStateFlow<List<UpdateItem>>(emptyList())
    val updates: StateFlow<List<UpdateItem>> = _updates.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        loadUpdates()
        observeAutoRefresh()
    }

    private fun observeAutoRefresh() {
        preferenceRepository.getAutoRefreshInterval().onEach { minutes ->
            autoRefreshJob?.cancel()
            if (minutes > 0) {
                autoRefreshJob = viewModelScope.launch {
                    while (true) {
                        delay(minutes * 60 * 1000L)
                        loadUpdates()
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun loadUpdates() {
        viewModelScope.launch {
            combine(
                libraryRepository.getLibraryAnime(),
                libraryRepository.getLibraryManga()
            ) { anime, manga ->
                val allItems = (anime.map { it.id to it.title } + manga.map { it.id to it.title })
                allItems.map { (id, title) ->
                    UpdateItem(
                        id = id,
                        title = title,
                        updateInfo = "Checked for updates",
                        timestamp = System.currentTimeMillis()
                    )
                }
            }.collect {
                _updates.value = it
            }
        }
    }
}

data class UpdateItem(
    val id: String,
    val title: String,
    val updateInfo: String,
    val timestamp: Long
)
