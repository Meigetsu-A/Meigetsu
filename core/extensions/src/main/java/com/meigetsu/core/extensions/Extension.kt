package com.meigetsu.core.extensions

import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter
import kotlinx.serialization.Serializable

@Serializable
data class ExtensionMetadata(
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val iconUrl: String?,
    val type: ExtensionType,
    val pkgName: String,
    val author: String,
    val isNsfw: Boolean = false,
    val lang: String = "en",
    val isTrusted: Boolean = false
)

enum class ExtensionType {
    ANIME, MANGA, BOTH
}

interface Extension {
    val metadata: ExtensionMetadata
}

interface AnimeProvider : Extension {
    suspend fun getEpisodes(animeId: String): List<Episode>
    suspend fun getStreamUrls(episode: Episode): List<StreamUrl>
    suspend fun search(query: String, page: Int): List<MediaSearchResult>
    suspend fun getPopular(page: Int): List<MediaSearchResult>
    suspend fun getLatest(page: Int): List<MediaSearchResult>
}

interface MangaProvider : Extension {
    suspend fun getChapters(mangaId: String): List<Chapter>
    suspend fun getPages(chapter: Chapter): List<String>
    suspend fun search(query: String, page: Int): List<MediaSearchResult>
    suspend fun getPopular(page: Int): List<MediaSearchResult>
    suspend fun getLatest(page: Int): List<MediaSearchResult>
}

@Serializable
data class StreamUrl(
    val url: String,
    val quality: String,
    val format: String = "mp4",
    val headers: Map<String, String> = emptyMap()
)

@Serializable
data class MediaSearchResult(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val type: String,
    val extensionId: String
)

@Serializable
data class ExtensionRemote(
    val pkg: String,
    val name: String,
    val version: String,
    val versionCode: Long,
    val libVersion: Double,
    val apk: String,
    val icon: String,
    val lang: String = "en",
    val isNsfw: Boolean = false,
    val repoUrl: String? = null
)
