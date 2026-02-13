package com.meigetsu.core.extensions.model

import com.meigetsu.core.extensions.*
import com.meigetsu.core.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ConsumetProvider @Inject constructor(
    private val client: HttpClient
) : AnimeProvider, MangaProvider {

    override val metadata = ExtensionMetadata(
        id = "consumet",
        name = "Consumet (Aggregator)",
        version = "1.1.0",
        description = "Powerful aggregator for multiple sources",
        iconUrl = "https://consumet.org/favicon.ico",
        type = ExtensionType.BOTH,
        pkgName = "com.meigetsu.extension.consumet",
        author = "Meigetsu",
        isTrusted = true
    )

    private val apiBase = "https://api.consumet.org" // Or a working mirror

    @Serializable
    private data class ConsumetSearchResponse(val results: List<ConsumetSearchResult>)
    @Serializable
    private data class ConsumetSearchResult(val id: String, val title: String, val image: String, val type: String? = null)

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        return try {
            val response: ConsumetSearchResponse = client.get("$apiBase/meta/anilist/$query?page=$page").body()
            response.results.map {
                MediaSearchResult(it.id, it.title, it.image, it.type ?: "ANIME", metadata.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getEpisodes(animeId: String): List<Episode> {
        return try {
            val response: ConsumetAnimeDetails = client.get("$apiBase/meta/anilist/info/$animeId").body()
            response.episodes.map {
                Episode(it.id, animeId, it.number, it.title, it.image, it.airDate)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        return try {
            val response: ConsumetStreamResponse = client.get("$apiBase/meta/anilist/watch/${episode.id}").body()
            response.sources.map {
                StreamUrl(it.url, it.quality, it.isM3U8.let { if (it) "hls" else "mp4" })
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getChapters(mangaId: String): List<Chapter> {
        return try {
            val response: ConsumetAnimeDetails = client.get("$apiBase/meta/anilist/info/$mangaId").body()
            response.chapters?.map {
                Chapter(it.id, mangaId, it.number.toDouble(), it.title, it.scanlator)
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPages(chapter: Chapter): List<String> {
        return try {
            val response: List<ConsumetPage> = client.get("$apiBase/meta/anilist/read/${chapter.id}").body()
            response.map { it.img }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPopular(page: Int): List<MediaSearchResult> = emptyList()
    override suspend fun getLatest(page: Int): List<MediaSearchResult> = emptyList()

    @Serializable
    private data class ConsumetAnimeDetails(val episodes: List<ConsumetEpisode> = emptyList(), val chapters: List<ConsumetChapter>? = null)
    @Serializable
    private data class ConsumetEpisode(val id: String, val title: String? = null, val number: Int, val image: String? = null, val airDate: String? = null)
    @Serializable
    private data class ConsumetChapter(val id: String, val title: String? = null, val number: Float, val scanlator: String? = null)
    @Serializable
    private data class ConsumetStreamResponse(val sources: List<ConsumetSource>)
    @Serializable
    private data class ConsumetSource(val url: String, val quality: String, val isM3U8: Boolean)
    @Serializable
    private data class ConsumetPage(val img: String, val page: Int)
}
