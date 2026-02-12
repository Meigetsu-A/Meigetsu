package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val mediaId: String,
    val episodeNumber: Int,
    val position: Long,
    val duration: Long,
    val lastWatched: Long
)

@Entity(tableName = "read_history")
data class ReadHistoryEntity(
    @PrimaryKey val mediaId: String,
    val chapterNumber: Double,
    val pageNumber: Int,
    val lastRead: Long
)
