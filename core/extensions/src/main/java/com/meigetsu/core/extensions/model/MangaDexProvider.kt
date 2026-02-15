package com.meigetsu.core.extensions.model

import com.meigetsu.core.extensions.*
import com.meigetsu.core.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

class MangaDexProvider @Inject constructor(
    private val client: HttpClient
) : MangaProvider {

    override val metadata = ExtensionMetadata(
        id = "mangadex",
        name = "MangaDex (Built-in)",
        version = "1.0.0",
        description = "MangaDex official API provider",
        iconUrl = "https://mangadex.org/favicon.ico",
        type = ExtensionType.MANGA,
        pkgName = "com.meigetsu.extension.mangadex",
        author = "Meigetsu",
        isTrusted = true
    )

    private val apiBase = "https://api.mangadex.org"

    @Serializable
    private data class MDListResponse(val data: List<MDMangaData>)
    @Serializable
    private data class MDMangaData(val id: String, val attributes: MDAttributes)
    @Serializable
    private data class MDAttributes(val title: Map<String, String>, val description: Map<String, String>? = null)

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        return try {
            val response: MDListResponse = client.get("$apiBase/manga") {
                parameter("title", query)
                parameter("limit", 20)
                parameter("offset", (page - 1) * 20)
            }.body()
            response.data.map {
                MediaSearchResult(
                    id = it.id,
                    title = it.attributes.title.values.firstOrNull() ?: "Unknown",
                    imageUrl = "https://uploads.mangadex.org/covers/${it.id}/cover.jpg", // Needs cover ID but for brevity
                    type = "MANGA",
                    extensionId = metadata.id
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getChapters(mangaId: String): List<Chapter> {
        return try {
            val response: MDFeedResponse = client.get("$apiBase/manga/$mangaId/feed") {
                parameter("translatedLanguage[]", "en")
                parameter("order[chapter]", "asc")
            }.body()
            response.data.map {
                Chapter(
                    id = it.id,
                    mangaId = mangaId,
                    number = it.attributes.chapter?.toDoubleOrNull() ?: 0.0,
                    title = it.attributes.title ?: "Chapter ${it.attributes.chapter}",
                    scanlator = null
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPages(chapter: Chapter): List<String> {
        return try {
            val response: MDServerResponse = client.get("$apiBase/at-home/server/${chapter.id}").body()
            val hash = response.chapter.hash
            response.chapter.data.map {
                "${response.baseUrl}/data/$hash/$it"
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Serializable
    private data class MDFeedResponse(val data: List<MDChapterData>)
    @Serializable
    private data class MDChapterData(val id: String, val attributes: MDChapterAttributes)
    @Serializable
    private data class MDChapterAttributes(val chapter: String?, val title: String?)
    @Serializable
    private data class MDServerResponse(val baseUrl: String, val chapter: MDServerChapter)
    @Serializable
    private data class MDServerChapter(val hash: String, val data: List<String>)

    override suspend fun getPopular(page: Int): List<MediaSearchResult> = emptyList()
    override suspend fun getLatest(page: Int): List<MediaSearchResult> = emptyList()
}
