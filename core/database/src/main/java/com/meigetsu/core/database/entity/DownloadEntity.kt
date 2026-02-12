package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String, // Combination of mediaId and episode/chapter id
    val mediaId: String,
    val mediaTitle: String,
    val mediaType: String, // ANIME or MANGA
    val itemTitle: String, // Episode name or Chapter name
    val itemId: String,
    val url: String,
    val filePath: String?,
    val status: DownloadStatus,
    val progress: Float,
    val totalSize: Long,
    val downloadedSize: Long,
    val extensionId: String
)

enum class DownloadStatus {
    QUEUED, DOWNLOADING, COMPLETED, FAILED, PAUSED
}
