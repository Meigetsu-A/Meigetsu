package com.meigetsu.core.domain.usecase

import com.meigetsu.core.domain.repository.DownloadRepository
import com.meigetsu.core.model.*
import javax.inject.Inject

class DownloadItemsUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {
    suspend fun execute(
        media: Anime, // Or Manga
        items: List<Episode>, // Or Chapter
        extensionId: String
    ) {
        items.forEach { item ->
            val download = Download(
                id = "${media.id}_${item.id}",
                mediaId = media.id,
                mediaTitle = media.title,
                mediaType = "ANIME",
                itemTitle = "Episode ${item.number}",
                itemId = item.id,
                url = "", // Will be fetched by worker or before starting
                filePath = null,
                status = DownloadStatus.QUEUED,
                progress = 0f,
                totalSize = 0,
                downloadedSize = 0,
                extensionId = extensionId
            )
            downloadRepository.startDownload(download)
        }
    }
}
