package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_items")
data class LibraryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverImage: String?,
    val type: String, // ANIME or MANGA
    val status: String, // WATCHING, COMPLETED, etc.
    val progress: Int,
    val totalEpisodes: Int?,
    val lastUpdated: Long,
    val categoryId: String? = null
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val order: Int
)
