package com.meigetsu.core.model

data class Download(
    val id: String,
    val mediaId: String,
    val mediaTitle: String,
    val mediaType: String,
    val itemTitle: String,
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
