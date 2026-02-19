package com.meigetsu.core.extensions

import android.content.Context
import com.meigetsu.core.model.SourceDefinition
import com.meigetsu.core.network.ScraperEngine
import com.meigetsu.core.network.LinkExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scraperEngine: ScraperEngine,
    private val linkExtractor: LinkExtractor
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _installedExtensions = MutableStateFlow<List<Extension>>(emptyList())
    val installedExtensions = _installedExtensions.asStateFlow()

    private val _disabledExtensionIds = MutableStateFlow<Set<String>>(emptySet())
    val disabledExtensionIds = _disabledExtensionIds.asStateFlow()

    private val _animeProviders = MutableStateFlow<Map<String, AnimeProvider>>(emptyMap())
    val animeProviders = _animeProviders.asStateFlow()

    private val _mangaProviders = MutableStateFlow<Map<String, MangaProvider>>(emptyMap())
    val mangaProviders = _mangaProviders.asStateFlow()

    init {
        loadDynamicExtensions()
        loadAssetExtensions()
    }

    private fun loadDynamicExtensions() {
        val extensionsDir = context.getExternalFilesDir("extensions") ?: return
        extensionsDir.listFiles()?.filter { it.extension == "json" }?.forEach { file ->
            try {
                val definition = json.decodeFromString<SourceDefinition>(file.readText())
                val provider = UniversalProvider(definition, scraperEngine, linkExtractor)
                registerExtension(provider)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadAssetExtensions() {
        try {
            context.assets.list("sources")?.forEach { fileName ->
                if (fileName.endsWith(".json")) {
                    val jsonString = context.assets.open("sources/$fileName").bufferedReader().use { it.readText() }
                    val definition = json.decodeFromString<SourceDefinition>(jsonString)
                    val provider = UniversalProvider(definition, scraperEngine, linkExtractor)
                    registerExtension(provider)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun registerExtension(extension: Extension) {
        val current = _installedExtensions.value
        if (current.none { it.metadata.id == extension.metadata.id }) {
            _installedExtensions.value = current + extension
            updateProviders()
        }
    }

    fun toggleExtension(id: String) {
        val current = _disabledExtensionIds.value
        if (current.contains(id)) {
            _disabledExtensionIds.value = current - id
        } else {
            _disabledExtensionIds.value = current + id
        }
        updateProviders()
    }

    private fun updateProviders() {
        val disabled = _disabledExtensionIds.value
        val installed = _installedExtensions.value

        _animeProviders.value = installed
            .filter { it is AnimeProvider && !disabled.contains(it.metadata.id) }
            .associate { it.metadata.id to it as AnimeProvider }

        _mangaProviders.value = installed
            .filter { it is MangaProvider && !disabled.contains(it.metadata.id) }
            .associate { it.metadata.id to it as MangaProvider }
    }

    fun getAnimeProvider(id: String): AnimeProvider? = _animeProviders.value[id]
    fun getMangaProvider(id: String): MangaProvider? = _mangaProviders.value[id]
}
