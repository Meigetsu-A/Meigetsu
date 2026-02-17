package com.meigetsu.core.domain.usecase

import com.meigetsu.core.domain.repository.DownloadRepository
import com.meigetsu.core.model.*
import javax.inject.Inject

class DownloadItemsUseCase @Inject constructor(
    private val downloadRepository: DownloadRepository
) {
    suspend fun execute(
        media: Media,
        episodes: List<Episode>,
        extensionId: String
    ) {
        episodes.forEach { item ->
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

    suspend fun executeChapters(
        media: Media,
        chapters: List<Chapter>,
        extensionId: String
    ) {
        chapters.forEach { item ->
            val download = Download(
                id = "${media.id}_${item.id}",
                mediaId = media.id,
                mediaTitle = media.title,
                mediaType = "MANGA",
                itemTitle = "Chapter ${item.number}",
                itemId = item.id,
                url = item.url,
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
