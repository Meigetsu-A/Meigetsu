package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.Download
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloads(): Flow<List<Download>>
    fun getDownloadsForMedia(mediaId: String): Flow<List<Download>>
    suspend fun startDownload(download: Download)
    suspend fun pauseDownload(id: String)
    suspend fun resumeDownload(id: String)
    suspend fun cancelDownload(id: String)
    suspend fun deleteDownload(id: String)
}
