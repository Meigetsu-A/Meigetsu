package com.meigetsu.core.extensions

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

@Singleton
class ExtensionManager @Inject constructor() {
    private val animeSources = mutableMapOf<String, AnimeSource>()
    private val mangaSources = mutableMapOf<String, MangaSource>()

    fun registerAnimeSource(source: AnimeSource) {
        animeSources[source.id] = source
    }

    fun registerMangaSource(source: MangaSource) {
        mangaSources[source.id] = source
    }

    // Simulate loading from JSON string
    fun loadExtensionFromJson(json: String) {
        // In a real app, this would use reflection or a DEX loader
        // Here we just parse the metadata
    }

    fun getAnimeSource(id: String): AnimeSource? = animeSources[id]
    fun getMangaSource(id: String): MangaSource? = mangaSources[id]

    fun getAllAnimeSources(): List<AnimeSource> = animeSources.values.toList()
    fun getAllMangaSources(): List<MangaSource> = mangaSources.values.toList()
}
