package com.meigetsu.core.extensions

import android.content.Context
import com.meigetsu.core.model.Episode
import dagger.hilt.android.qualifiers.ApplicationContext
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
import java.io.File

@Singleton
class ExtensionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private val _installedExtensions = MutableStateFlow<List<Extension>>(emptyList())
    val installedExtensions = _installedExtensions.asStateFlow()

    private val _animeProviders = MutableStateFlow<Map<String, AnimeProvider>>(emptyMap())
    val animeProviders = _animeProviders.asStateFlow()

    private val _mangaProviders = MutableStateFlow<Map<String, MangaProvider>>(emptyMap())
    val mangaProviders = _mangaProviders.asStateFlow()

    init {
        registerExtension(SampleAnimeExtension())
    }

    suspend fun fetchExtensions(repoUrl: String): List<ExtensionMetadata> {
        return try {
            client.get(repoUrl).body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun registerExtension(extension: Extension) {
        _installedExtensions.value += extension
        if (extension is AnimeProvider) {
            _animeProviders.value += (extension.metadata.id to extension)
        }
        if (extension is MangaProvider) {
            _mangaProviders.value += (extension.metadata.id to extension)
        }
    }

    suspend fun loadExtensionFromApk(apkFile: File) {
        val dexDir = File(context.codeCacheDir, "extensions_dex").apply { mkdirs() }
        val classLoader = dalvik.system.DexClassLoader(
            apkFile.absolutePath,
            dexDir.absolutePath,
            null,
            context.classLoader
        )

        // In a real app, we would scan the APK for classes implementing Extension
        // For this demo/scaffold, we assume a specific class name or use a manifest
        try {
            val extensionClass = classLoader.loadClass("com.meigetsu.extension.ExternalExtension")
            val extension = extensionClass.getDeclaredConstructor().newInstance() as Extension
            registerExtension(extension)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAnimeProvider(id: String): AnimeProvider? = _animeProviders.value[id]
    fun getMangaProvider(id: String): MangaProvider? = _mangaProviders.value[id]
}

class SampleAnimeExtension : AnimeProvider {
    override val metadata = ExtensionMetadata(
        id = "sample-anime",
        name = "Meigetsu Sample",
        version = "1.0.0",
        description = "Official sample provider",
        iconUrl = null,
        type = ExtensionType.ANIME,
        pkgName = "com.meigetsu.extension.sample",
        author = "Meigetsu Team"
    )

    override suspend fun getEpisodes(animeId: String): List<Episode> = listOf(
        Episode("1", animeId, 1, "The Beginning", "https://img.aniworld.to/media/episode/cover/1.jpg", "2024-01-01"),
        Episode("2", animeId, 2, "The Journey", "https://img.aniworld.to/media/episode/cover/2.jpg", "2024-01-08")
    )

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> = listOf(
        StreamUrl("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "Auto", "m3u8")
    )

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        if (query.lowercase().contains("sample")) {
            return listOf(
                MediaSearchResult("1", "Sample Anime", null, "ANIME", metadata.id)
            )
        }
        return emptyList()
    }
    override suspend fun getPopular(page: Int): List<MediaSearchResult> = emptyList()
    override suspend fun getLatest(page: Int): List<MediaSearchResult> = emptyList()
}
