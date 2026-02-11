package com.meigetsu.core.domain.repository

import com.meigetsu.core.common.Resource
import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import com.meigetsu.core.model.Character
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getTrendingAnime(): Flow<Resource<List<Anime>>>
    fun getTrendingManga(): Flow<Resource<List<Manga>>>
    fun searchAnime(query: String, page: Int): Flow<Resource<List<Anime>>>
    fun searchManga(query: String, page: Int): Flow<Resource<List<Manga>>>
    fun getAnimeDetails(id: String): Flow<Resource<Anime>>
    fun getMangaDetails(id: String): Flow<Resource<Manga>>
    fun getCharacterDetails(id: String): Flow<Resource<Character>>
}
