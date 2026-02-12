package com.meigetsu.core.data.usecase

import com.meigetsu.core.extensions.*
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class GlobalSearchUseCase @Inject constructor(
    private val extensionManager: ExtensionManager
) {
    suspend fun execute(query: String): List<MediaSearchResult> = coroutineScope {
        val animeSearch = extensionManager.animeProviders.value.values.map { provider ->
            async { try { provider.search(query, 1) } catch (e: Exception) { emptyList<MediaSearchResult>() } }
        }
        val mangaSearch = extensionManager.mangaProviders.value.values.map { provider ->
            async { try { provider.search(query, 1) } catch (e: Exception) { emptyList<MediaSearchResult>() } }
        }

        (animeSearch + mangaSearch).awaitAll().flatten()
    }
}
