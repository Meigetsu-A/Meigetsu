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

    private val _availableExtensions = MutableStateFlow<List<ExtensionRemote>>(emptyList())
    val availableExtensions = _availableExtensions.asStateFlow()

    suspend fun fetchExtensions(repoUrl: String) {
        try {
            val url = if (repoUrl.endsWith("index.json")) repoUrl else "$repoUrl/index.json"
            val response: List<ExtensionRemote> = client.get(url).body()
            _availableExtensions.value = (_availableExtensions.value + response.map { it.copy(repoUrl = repoUrl) }).distinctBy { it.pkg }
        } catch (e: Exception) {
            e.printStackTrace()
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

    suspend fun installExtension(remote: ExtensionRemote) {
        try {
            val apkFile = File(context.cacheDir, "${remote.pkg}.apk")
            val bytes: ByteArray = client.get(remote.apk).body()
            apkFile.writeBytes(bytes)
            loadExtensionFromApk(apkFile)
        } catch (e: Exception) {
            e.printStackTrace()
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
