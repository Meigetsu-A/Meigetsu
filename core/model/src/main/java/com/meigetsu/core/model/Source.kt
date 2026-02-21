package com.meigetsu.core.model

interface Source {
    val name: String
    val baseUrl: String
    val type: MediaType

    suspend fun search(query: String): List<SourceSearchResult>
    suspend fun getLatest(page: Int): List<SourceSearchResult>

    // For Anime
    suspend fun getEpisodes(id: String): List<SourceEpisode>
    suspend fun getStreamUrl(episode: SourceEpisode): String

    // For Manga/Novel
    suspend fun getChapters(id: String): List<SourceChapter>
    suspend fun getPages(chapter: SourceChapter): List<String>
}

data class SourceSearchResult(
    val id: String,
    val title: String,
    val image: String?
)

data class SourceEpisode(
    val id: String,
    val number: Int,
    val title: String?,
    val url: String
)

data class SourceChapter(
    val id: String,
    val number: Double,
    val title: String?,
    val url: String,
    val date: String? = null,
    val wordCount: Int? = null
)
