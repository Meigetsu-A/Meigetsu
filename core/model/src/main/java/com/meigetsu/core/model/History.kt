package com.meigetsu.core.model

data class WatchHistory(
    val mediaId: String,
    val episodeNumber: Int,
    val position: Long,
    val duration: Long,
    val lastWatched: Long
)

data class ReadHistory(
    val mediaId: String,
    val chapterNumber: Double,
    val pageNumber: Int,
    val lastRead: Long
)
