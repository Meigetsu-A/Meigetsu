package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library")
data class LibraryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverImage: String?,
    val type: String, // ANIME, MANGA, NOVEL
    val status: String,
    val lastUpdated: Long
)
