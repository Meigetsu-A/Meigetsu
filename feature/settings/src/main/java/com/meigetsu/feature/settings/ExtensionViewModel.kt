package com.meigetsu.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.database.dao.RepoDao
import com.meigetsu.core.database.entity.ExtensionRepoEntity
import com.meigetsu.core.extensions.ExtensionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionViewModel @Inject constructor(
    val extensionManager: ExtensionManager,
    private val repoDao: RepoDao
) : ViewModel() {
    val repos: StateFlow<List<ExtensionRepoEntity>> = repoDao.getAllRepos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRepo(url: String) {
        viewModelScope.launch {
            val entity = ExtensionRepoEntity(url, "Custom Repo", System.currentTimeMillis())
            repoDao.insertRepo(entity)
            extensionManager.fetchExtensions(url)
        }
    }

    fun deleteRepo(repo: ExtensionRepoEntity) {
        viewModelScope.launch {
            repoDao.deleteRepo(repo)
        }
    }

    fun toggleTrust(repo: ExtensionRepoEntity) {
        viewModelScope.launch {
            repoDao.updateTrust(repo.url, !repo.isTrusted)
        }
    }

    fun scanExtensions() {
        extensionManager.scanInstalledExtensions()
    }
}
