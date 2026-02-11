package com.meigetsu.core.extensions

import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter

interface Extension {
    val name: String
    val version: String
    val id: String
}

interface AnimeSource : Extension {
    suspend fun getStreamUrls(episode: Episode): List<StreamUrl>
}

interface MangaSource : Extension {
    suspend fun getPages(chapter: Chapter): List<String>
}

data class StreamUrl(
    val url: String,
    val quality: String,
    val format: String = "mp4"
)
