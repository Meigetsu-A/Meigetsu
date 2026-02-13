package com.meigetsu.core.extensions

import android.content.Context
import com.meigetsu.core.database.dao.RepoDao
import com.meigetsu.core.database.entity.ExtensionRepoEntity
import com.meigetsu.core.extensions.model.AniListProvider
import com.meigetsu.core.extensions.model.MangaDexProvider
import com.meigetsu.core.extensions.model.ConsumetProvider
import com.meigetsu.core.model.Episode
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

@Singleton
class ExtensionManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: HttpClient,
    private val repoDao: RepoDao,
    private val aniListProvider: AniListProvider,
    private val mangaDexProvider: MangaDexProvider,
    private val consumetProvider: ConsumetProvider
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _installedExtensions = MutableStateFlow<List<Extension>>(emptyList())
    val installedExtensions = _installedExtensions.asStateFlow()

    private val _animeProviders = MutableStateFlow<Map<String, AnimeProvider>>(emptyMap())
    val animeProviders = _animeProviders.asStateFlow()

    private val _mangaProviders = MutableStateFlow<Map<String, MangaProvider>>(emptyMap())
    val mangaProviders = _mangaProviders.asStateFlow()

    private val _availableExtensions = MutableStateFlow<List<ExtensionRemote>>(emptyList())
    val availableExtensions = _availableExtensions.asStateFlow()

    init {
        registerExtension(aniListProvider)
        registerExtension(mangaDexProvider)
        registerExtension(consumetProvider)

        scope.launch {
            repoDao.getAllRepos().collect { repos ->
                if (repos.isEmpty()) {
                    repoDao.insertRepo(ExtensionRepoEntity(
                        url = "https://raw.githubusercontent.com/keiyoushi/extensions/repo/index.json",
                        name = "Keiyoushi",
                        isTrusted = true,
                        lastUpdated = System.currentTimeMillis()
                    ))
                }
                repos.forEach { fetchExtensions(it.url) }
            }
        }
    }

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
            loadExtensionFromApk(apkFile, remote.pkg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadExtensionFromApk(apkFile: File, pkgName: String? = null) {
        val dexDir = File(context.codeCacheDir, "extensions_dex").apply { mkdirs() }
        val classLoader = dalvik.system.DexClassLoader(
            apkFile.absolutePath,
            dexDir.absolutePath,
            null,
            context.classLoader
        )

        // Discovery logic: Try package name + .ExtensionEntry, then fallback to common names
        val possibleClasses = mutableListOf<String>()
        if (pkgName != null) {
            possibleClasses.add("$pkgName.ExtensionEntry")
            possibleClasses.add("$pkgName.Source")
        }
        possibleClasses.add("com.meigetsu.extension.ExternalExtension")
        possibleClasses.add("com.meigetsu.extension.ExtensionImpl")

        for (className in possibleClasses) {
            try {
                val extensionClass = classLoader.loadClass(className)
                val extension = extensionClass.getDeclaredConstructor().newInstance() as Extension
                registerExtension(extension)
                return // Success
            } catch (e: Exception) {
                // Try next
            }
        }
    }

    fun getAnimeProvider(id: String): AnimeProvider? = _animeProviders.value[id]
    fun getMangaProvider(id: String): MangaProvider? = _mangaProviders.value[id]

    fun addRepository(url: String, name: String) {
        scope.launch {
            repoDao.insertRepo(ExtensionRepoEntity(url = url, name = name, lastUpdated = System.currentTimeMillis()))
            fetchExtensions(url)
        }
    }

    fun removeRepository(url: String) {
        scope.launch {
            repoDao.deleteRepo(ExtensionRepoEntity(url = url, name = "", lastUpdated = 0))
            // Optionally clear available extensions from this repo
        }
    }

    fun getRepositories(): Flow<List<ExtensionRepoEntity>> = repoDao.getAllRepos()
}
