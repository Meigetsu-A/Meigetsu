package com.meigetsu.core.extensions

import com.meigetsu.core.model.SourceDefinition
import com.meigetsu.core.network.ScraperEngine
import com.meigetsu.core.network.LinkExtractor
import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter
import com.meigetsu.core.extensions.ExtensionType

class UniversalProvider(
    val definition: SourceDefinition,
    private val scraperEngine: ScraperEngine,
    private val linkExtractor: LinkExtractor? = null
) : AnimeProvider, MangaProvider {

    override val metadata: ExtensionMetadata = ExtensionMetadata(
        id = definition.id,
        name = definition.name,
        version = definition.version,
        description = "Automatically generated provider for ${definition.name}",
        iconUrl = null,
        type = if (definition.type == "ANIME") ExtensionType.ANIME else ExtensionType.MANGA,
        pkgName = "com.meigetsu.extension.${definition.id}",
        author = "Meigetsu Community"
    )

    override suspend fun getEpisodes(animeId: String): List<Episode> {
        val action = definition.episodes ?: return emptyList()
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("id" to animeId), definition.headers)
        return results.mapIndexed { index, map ->
            Episode(
                id = map["id"] ?: index.toString(),
                animeId = animeId,
                number = map["number"]?.toIntOrNull() ?: (index + 1),
                title = map["title"],
                thumbnail = map["thumbnail"],
                airDate = map["airDate"],
                url = map["url"] ?: ""
            )
        }
    }

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        val action = definition.streamUrls ?: return emptyList()
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("url" to episode.url), definition.headers)
        return results.map { map ->
            val rawUrl = map["url"] ?: ""
            val resolvedUrl = if (linkExtractor != null) linkExtractor.extract(rawUrl) else rawUrl
            StreamUrl(
                url = resolvedUrl,
                quality = map["quality"] ?: "Auto"
            )
        }
    }

    override suspend fun getChapters(mangaId: String): List<Chapter> {
        val action = definition.chapters ?: return emptyList()
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("id" to mangaId), definition.headers)
        return results.mapIndexed { index, map ->
            Chapter(
                id = map["id"] ?: index.toString(),
                mangaId = mangaId,
                number = map["number"]?.toDoubleOrNull() ?: (index + 1).toDouble(),
                title = map["title"] ?: "Chapter ${index + 1}",
                scanlator = map["scanlator"],
                url = map["url"] ?: ""
            )
        }
    }

    override suspend fun getPages(chapter: Chapter): List<String> {
        val action = definition.pages ?: return emptyList()
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("url" to chapter.url), definition.headers)
        return results.mapNotNull { it["url"] }
    }

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        val action = definition.search
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("query" to query, "page" to page.toString()), definition.headers)
        return results.map { map ->
            MediaSearchResult(
                id = map["id"] ?: "",
                title = map["title"] ?: "Unknown",
                imageUrl = map["image"],
                type = definition.type,
                extensionId = definition.id
            )
        }
    }

    override suspend fun getPopular(page: Int): List<MediaSearchResult> {
        val action = definition.popular
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("page" to page.toString()), definition.headers)
        return results.map { map ->
            MediaSearchResult(
                id = map["id"] ?: "",
                title = map["title"] ?: "Unknown",
                imageUrl = map["image"],
                type = definition.type,
                extensionId = definition.id
            )
        }
    }

    override suspend fun getLatest(page: Int): List<MediaSearchResult> {
        val action = definition.latest
        val results = scraperEngine.executeAction(definition.baseUrl, action, mapOf("page" to page.toString()), definition.headers)
        return results.map { map ->
            MediaSearchResult(
                id = map["id"] ?: "",
                title = map["title"] ?: "Unknown",
                imageUrl = map["image"],
                type = definition.type,
                extensionId = definition.id
            )
        }
    }
}
