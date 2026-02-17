package com.meigetsu.core.data.repository

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import com.meigetsu.core.database.dao.DownloadDao
import com.meigetsu.core.database.entity.DownloadEntity
import com.meigetsu.core.domain.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import com.meigetsu.core.model.Download
import com.meigetsu.core.model.DownloadStatus
import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.AnimeProvider
import com.meigetsu.core.extensions.MangaProvider
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
    private val downloadDao: DownloadDao,
    private val extensionManager: ExtensionManager
) : CoroutineWorker(context, workerParams) {

    private val client = io.ktor.client.HttpClient(io.ktor.client.engine.okhttp.OkHttp)

    override suspend fun doWork(): Result {
        val id = inputData.getString("download_id") ?: return Result.failure()
        var download = downloadDao.getDownloadById(id) ?: return Result.failure()

        try {
            updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.DOWNLOADING, 0f)

            var downloadUrl = download.url
            if (download.mediaType == "MANGA") {
                val provider = extensionManager.mangaProviders.value[download.extensionId] ?: return Result.failure()
                val pages = provider.getPages(Chapter(download.itemId, download.mediaId, 0.0, download.itemTitle, null))
                if (pages.isEmpty()) return Result.failure()

                val dir = java.io.File(context.getExternalFilesDir(null), "${download.mediaId}/${download.itemId}")
                dir.mkdirs()

                pages.forEachIndexed { index, pageUrl ->
                    val pageFile = java.io.File(dir, "${index + 1}.jpg")
                    val response = client.get(pageUrl)
                    pageFile.writeBytes(response.readBytes())
                    updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.DOWNLOADING, (index + 1).toFloat() / pages.size)
                    if (isStopped) return Result.retry()
                }
            } else {
                if (downloadUrl.isBlank()) {
                    val provider = extensionManager.animeProviders.value[download.extensionId] ?: return Result.failure()
                    val streams = provider.getStreamUrls(Episode(download.itemId, download.mediaId, 0, download.itemTitle, null, null))
                    downloadUrl = streams.firstOrNull()?.url ?: ""
                    if (downloadUrl.isBlank()) return Result.failure()
                    downloadDao.updateUrl(id, downloadUrl)
                }

                val filePath = download.filePath ?: "${download.mediaId}/${download.itemId}.mp4"
                val file = java.io.File(context.getExternalFilesDir(null), filePath)
                file.parentFile?.mkdirs()

                client.prepareGet(downloadUrl).execute { httpResponse ->
                    val totalBytes = httpResponse.contentLength() ?: -1L
                    var downloadedBytes = 0L
                    val channel = httpResponse.bodyAsChannel()

                    file.outputStream().use { output ->
                        val buffer = ByteArray(1024 * 8)
                        while (!channel.isClosedForRead) {
                            val read = channel.readAvailable(buffer)
                            if (read == -1) break
                            output.write(buffer, 0, read)
                            downloadedBytes += read
                            if (totalBytes > 0) {
                                val progress = downloadedBytes.toFloat() / totalBytes
                                updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.DOWNLOADING, progress, downloadedBytes)
                            }
                            if (isStopped) break
                        }
                    }
                }
            }

            if (isStopped) {
                updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.PAUSED, 0f)
                return Result.retry()
            }

            updateStatus(id, com.meigetsu.core.database.entity.DownloadStatus.COMPLETED, 1f)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
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
