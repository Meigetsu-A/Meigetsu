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

    override suspend fun getChapters(mangaId: String): List<Chapter> {
        // Implementation for MangaDex API
        return emptyList() // Placeholder for brevity, but real in final
    }

    override suspend fun getPages(chapter: Chapter): List<String> = emptyList()

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        return emptyList()
    }

    override suspend fun getPopular(page: Int): List<MediaSearchResult> = emptyList()
    override suspend fun getLatest(page: Int): List<MediaSearchResult> = emptyList()
}
