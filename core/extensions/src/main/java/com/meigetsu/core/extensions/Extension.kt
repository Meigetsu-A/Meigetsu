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
    val type: String, // "ANIME" or "MANGA"
    val baseUrl: String,
    val apiUrl: String? = null
)

interface Extension {
    val metadata: ExtensionMetadata
}

interface AnimeSource : Extension {
    suspend fun getStreamUrls(episode: Episode): List<StreamUrl>
    suspend fun search(query: String, page: Int): List<MediaSearchResult>
}

interface MangaSource : Extension {
    suspend fun getPages(chapter: Chapter): List<String>
    suspend fun search(query: String, page: Int): List<MediaSearchResult>
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
    val type: String
)
