package com.meigetsu.core.domain.repository

import com.meigetsu.core.common.Resource
import com.meigetsu.core.model.Anime
import com.meigetsu.core.model.Manga
import com.meigetsu.core.model.Character
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getTrendingAnime(): Flow<Resource<List<Anime>>>
    fun getTrendingManga(): Flow<Resource<List<Manga>>>
    fun getPopularAnime(): Flow<Resource<List<Anime>>>
    fun getPopularManga(): Flow<Resource<List<Manga>>>
    fun getRecommendedAnime(): Flow<Resource<List<Anime>>>
    fun searchAnime(query: String, page: Int): Flow<Resource<List<Anime>>>
    fun searchManga(query: String, page: Int): Flow<Resource<List<Manga>>>
    fun searchCharacters(query: String): Flow<Resource<List<Character>>>
    fun getAnimeDetails(id: String): Flow<Resource<Anime>>
    fun getMangaDetails(id: String): Flow<Resource<Manga>>
    fun getCharacterDetails(id: String): Flow<Resource<Character>>
    fun getMultipleAnime(ids: List<String>): Flow<Resource<List<Anime>>>
}
