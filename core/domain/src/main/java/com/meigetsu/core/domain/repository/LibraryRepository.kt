package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
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

    fun getWatchHistory(): Flow<List<String>> // Returns IDs
}
