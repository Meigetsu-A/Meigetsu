package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import com.meigetsu.core.model.Category
import com.meigetsu.core.model.WatchHistory
import com.meigetsu.core.model.ReadHistory
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryAnime(): Flow<List<Anime>>
    fun getLibraryManga(): Flow<List<Manga>>
    fun getFavoriteCharacters(): Flow<List<com.meigetsu.core.model.Character>>
    suspend fun addToLibrary(anime: Anime)
    suspend fun addToLibrary(manga: Manga)
    suspend fun addCharacterToLibrary(character: com.meigetsu.core.model.Character)
    suspend fun removeFromLibrary(id: String)
    suspend fun removeCharacterFromLibrary(id: String)

    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(name: String)
    suspend fun deleteCategory(id: String)
    suspend fun updateItemsCategory(ids: List<String>, categoryId: String?)
    suspend fun updateItemsStatus(ids: List<String>, status: String)

    fun getWatchHistory(): Flow<List<String>> // Returns IDs
    suspend fun getWatchHistoryById(mediaId: String): WatchHistory?
    fun getReadHistory(): Flow<List<String>>
    suspend fun getReadHistoryById(mediaId: String): ReadHistory?

    suspend fun updateWatchHistory(mediaId: String, episode: Int, position: Long, duration: Long)
    suspend fun updateReadHistory(mediaId: String, chapter: Double, page: Int)
}
