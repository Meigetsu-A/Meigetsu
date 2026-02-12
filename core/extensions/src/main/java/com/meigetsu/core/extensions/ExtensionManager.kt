package com.meigetsu.core.extensions

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class ExtensionManager @Inject constructor() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val _animeSources = MutableStateFlow<Map<String, AnimeSource>>(emptyMap())
    val animeSources = _animeSources.asStateFlow()

    private val _mangaSources = MutableStateFlow<Map<String, MangaSource>>(emptyMap())
    val mangaSources = _mangaSources.asStateFlow()

    init {
        // Register default sample source
        registerAnimeSource(SampleAnimeSource())
    }

    suspend fun fetchExtensions(repoUrl: String): List<ExtensionMetadata> {
        return try {
            client.get(repoUrl).body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun registerAnimeSource(source: AnimeSource) {
        _animeSources.value = _animeSources.value + (source.metadata.id to source)
    }

    fun registerMangaSource(source: MangaSource) {
        _mangaSources.value = _mangaSources.value + (source.metadata.id to source)
    }

    fun getAnimeSource(id: String): AnimeSource? = _animeSources.value[id]
    fun getMangaSource(id: String): MangaSource? = _mangaSources.value[id]
}
