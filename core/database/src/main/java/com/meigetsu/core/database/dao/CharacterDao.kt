package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM favorite_characters")
    fun getAllFavoriteCharacters(): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("DELETE FROM favorite_characters WHERE id = :id")
    suspend fun deleteCharacterById(id: String)

    @Query("SELECT EXISTS(SELECT * FROM favorite_characters WHERE id = :id)")
    fun isCharacterFavorite(id: String): Flow<Boolean>
}
