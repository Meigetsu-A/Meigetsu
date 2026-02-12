package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryAnime(): Flow<List<Anime>>
    fun getLibraryManga(): Flow<List<Manga>>
    suspend fun addToLibrary(anime: Anime)
    suspend fun addToLibrary(manga: Manga)
    suspend fun removeFromLibrary(id: String)

    fun getWatchHistory(): Flow<List<String>> // Returns IDs
}
