package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trackers")
data class TrackerEntity(
    @PrimaryKey
    val trackerName: String, // MAL, KITSU, ANILIST
    val accessToken: String,
    val username: String
)
