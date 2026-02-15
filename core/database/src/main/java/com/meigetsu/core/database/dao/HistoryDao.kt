package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.WatchHistoryEntity
import com.meigetsu.core.database.entity.ReadHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatched DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE mediaId = :mediaId")
    suspend fun getWatchHistoryById(mediaId: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchHistory(history: WatchHistoryEntity)

    @Query("SELECT * FROM read_history ORDER BY lastRead DESC")
    fun getReadHistory(): Flow<List<ReadHistoryEntity>>

    @Query("SELECT * FROM read_history WHERE mediaId = :mediaId")
    suspend fun getReadHistoryById(mediaId: String): ReadHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadHistory(history: ReadHistoryEntity)
}
