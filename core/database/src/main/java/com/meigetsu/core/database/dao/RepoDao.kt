package com.meigetsu.core.database.dao

import androidx.room.*
import com.meigetsu.core.database.entity.ExtensionRepoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM extension_repos")
    fun getAllRepos(): Flow<List<ExtensionRepoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: ExtensionRepoEntity)

    @Delete
    suspend fun deleteRepo(repo: ExtensionRepoEntity)

    @Query("UPDATE extension_repos SET isTrusted = :trusted WHERE url = :url")
    suspend fun updateTrust(url: String, trusted: Boolean)
}
