package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_stats")
data class ReadingStatsEntity(
    @PrimaryKey
    val date: Long, // Day timestamp
    val chaptersRead: Int,
    val minutesSpent: Int,
    val mediaType: String // ANIME or MANGA
)
