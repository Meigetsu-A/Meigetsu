package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.DownloadEntity
import com.meigetsu.core.database.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloads")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE mediaId = :mediaId")
    fun getDownloadsForMedia(mediaId: String): Flow<List<DownloadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Query("UPDATE downloads SET status = :status, progress = :progress, downloadedSize = :downloadedSize WHERE id = :id")
    suspend fun updateProgress(id: String, status: DownloadStatus, progress: Float, downloadedSize: Long)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: String)
}
