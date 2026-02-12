package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.ReadingStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingStatsDao {
    @Query("SELECT * FROM reading_stats ORDER BY date DESC")
    fun getAllStats(): Flow<List<ReadingStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStat(stat: ReadingStatsEntity)

    @Query("UPDATE reading_stats SET chaptersRead = chaptersRead + 1 WHERE date = :date AND mediaType = :type")
    suspend fun incrementChapters(date: Long, type: String)
}
