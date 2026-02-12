package com.meigetsu.core.data.repository

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.meigetsu.core.database.dao.DownloadDao
import com.meigetsu.core.database.entity.DownloadEntity
import com.meigetsu.core.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.meigetsu.core.model.Download
import com.meigetsu.core.model.DownloadStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao
) : DownloadRepository {

    override fun getAllDownloads(): Flow<List<Download>> =
        downloadDao.getAllDownloads().map { entities -> entities.map { it.toDomain() } }

    override fun getDownloadsForMedia(mediaId: String): Flow<List<Download>> =
        downloadDao.getDownloadsForMedia(mediaId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun startDownload(download: Download) {
        downloadDao.insertDownload(download.toEntity())

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf("download_id" to download.id))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .addTag(download.id)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            download.id,
            ExistingWorkPolicy.REPLACE,
            downloadRequest
        )
    }

    override suspend fun pauseDownload(id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(id)
        // Update status in DB
    }

    override suspend fun resumeDownload(id: String) {
        // Logic to resume
    }

    override suspend fun cancelDownload(id: String) {
        WorkManager.getInstance(context).cancelUniqueWork(id)
        downloadDao.deleteDownloadById(id)
    }

    override suspend fun deleteDownload(id: String) {
        cancelDownload(id)
        // Delete files
    }

    private fun DownloadEntity.toDomain() = Download(
        id = id,
        mediaId = mediaId,
        mediaTitle = mediaTitle,
        mediaType = mediaType,
        itemTitle = itemTitle,
        itemId = itemId,
        url = url,
        filePath = filePath,
        status = DownloadStatus.valueOf(status.name),
        progress = progress,
        totalSize = totalSize,
        downloadedSize = downloadedSize,
        extensionId = extensionId
    )

    private fun Download.toEntity() = DownloadEntity(
        id = id,
        mediaId = mediaId,
        mediaTitle = mediaTitle,
        mediaType = mediaType,
        itemTitle = itemTitle,
        itemId = itemId,
        url = url,
        filePath = filePath,
        status = com.meigetsu.core.database.entity.DownloadStatus.valueOf(status.name),
        progress = progress,
        totalSize = totalSize,
        downloadedSize = downloadedSize,
        extensionId = extensionId
    )
}

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(context, workerParams) {

    private val client = io.ktor.client.HttpClient(io.ktor.client.engine.okhttp.OkHttp)

    override suspend fun doWork(): Result {
        val id = inputData.getString("download_id") ?: return Result.failure()

        try {
            updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.DOWNLOADING, 0f)

            // In a real production app, we would fetch the actual download URL here
            // val url = provider.getStreamUrls(episode).first().url
            // For now, we simulate a 10MB download
            val totalBytes = 1024L * 1024L * 10L
            var downloadedBytes = 0L

            while (downloadedBytes < totalBytes) {
                kotlinx.coroutines.delay(200)
                downloadedBytes += 1024L * 512L // 512KB chunks
                val progress = downloadedBytes.toFloat() / totalBytes
                updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.DOWNLOADING, progress, downloadedBytes)
                if (isStopped) return Result.retry()
            }

            updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.COMPLETED, 1f, totalBytes)
            return Result.success()
        } catch (e: Exception) {
            updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.FAILED, 0f)
            return Result.failure()
        }
    }

    private suspend fun updateStatus(
        id: String,
        status: com.meigetsu.core.database.entity.DownloadStatus,
        progress: Float,
        downloadedSize: Long = 0
    ) {
        downloadDao.updateProgress(id, status, progress, downloadedSize)
    }
}
