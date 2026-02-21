package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library")
    fun getAll(): Flow<List<LibraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LibraryEntity)

    @Query("DELETE FROM library WHERE id = :id")
    suspend fun delete(id: String)
}
