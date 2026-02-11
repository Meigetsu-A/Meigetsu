package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.CategoryEntity
import com.meigetsu.core.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items")
    fun getAllLibraryItems(): Flow<List<LibraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibraryItem(item: LibraryEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteLibraryItem(id: String)

    @Query("SELECT * FROM categories ORDER BY `order` ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)
}
