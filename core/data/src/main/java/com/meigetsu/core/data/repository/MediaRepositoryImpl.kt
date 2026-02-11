package com.meigetsu.core.data.repository

import com.apollographql.apollo.ApolloClient
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.GetCharacterDetailsQuery
import com.meigetsu.core.network.GetMediaDetailsQuery
import com.meigetsu.core.network.GetTrendingMediaQuery
import com.meigetsu.core.network.SearchMediaQuery
import com.meigetsu.core.network.type.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : MediaRepository {

    override fun getTrendingAnime(): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetTrendingMediaQuery(type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME))).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnime()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTrendingManga(): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetTrendingMediaQuery(type = com.apollographql.apollo.api.Optional.present(MediaType.MANGA))).execute()
            val mangaList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toManga()
            } ?: emptyList()
            emit(Resource.Success(mangaList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchAnime(query: String, page: Int): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(SearchMediaQuery(
                search = com.apollographql.apollo.api.Optional.present(query),
                type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME),
                page = com.apollographql.apollo.api.Optional.present(page)
            )).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnimeSearch()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchManga(query: String, page: Int): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(SearchMediaQuery(
                search = com.apollographql.apollo.api.Optional.present(query),
                type = com.apollographql.apollo.api.Optional.present(MediaType.MANGA),
                page = com.apollographql.apollo.api.Optional.present(page)
            )).execute()
            val mangaList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toMangaSearch()
            } ?: emptyList()
            emit(Resource.Success(mangaList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getAnimeDetails(id: String): Flow<Resource<Anime>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(id.toInt()))).execute()
            val anime = response.data?.Media?.toAnimeDetails()
            if (anime != null) {
                emit(Resource.Success(anime))
            } else {
                emit(Resource.Error("Anime not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getMangaDetails(id: String): Flow<Resource<Manga>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(id.toInt()))).execute()
            val manga = response.data?.Media?.toMangaDetails()
            if (manga != null) {
                emit(Resource.Success(manga))
            } else {
                emit(Resource.Error("Manga not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getCharacterDetails(id: String): Flow<Resource<Character>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetCharacterDetailsQuery(id = com.apollographql.apollo.api.Optional.present(id.toInt()))).execute()
            val char = response.data?.Character
            if (char != null) {
                emit(Resource.Success(Character(
                    id = char.id.toString(),
                    name = char.name?.full ?: "Unknown",
                    image = char.image?.large,
                    description = char.description
                )))
            } else {
                emit(Resource.Error("Character not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    private fun GetTrendingMediaQuery.Medium.toAnime() = Anime(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = null,
        coverImage = coverImage?.extraLarge,
        bannerImage = null,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.TV,
        episodes = null,
        nextEpisode = null,
        genres = emptyList(),
        averageScore = averageScore,
        popularity = null,
        season = null,
        year = null,
        studio = null
    )

    private fun GetTrendingMediaQuery.Medium.toManga() = Manga(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = null,
        coverImage = coverImage?.extraLarge,
        bannerImage = null,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.MANGA,
        chapters = null,
        volumes = null,
        genres = emptyList(),
        averageScore = averageScore,
        popularity = null
    )

    private fun SearchMediaQuery.Medium.toAnimeSearch() = Anime(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = null,
        coverImage = coverImage?.extraLarge,
        bannerImage = null,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.TV,
        episodes = null,
        nextEpisode = null,
        genres = emptyList(),
        averageScore = averageScore,
        popularity = null,
        season = null,
        year = null,
        studio = null
    )

    private fun SearchMediaQuery.Medium.toMangaSearch() = Manga(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = null,
        coverImage = coverImage?.extraLarge,
        bannerImage = null,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.MANGA,
        chapters = null,
        volumes = null,
        genres = emptyList(),
        averageScore = averageScore,
        popularity = null
    )

    private fun GetMediaDetailsQuery.Media.toAnimeDetails() = Anime(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = description,
        coverImage = coverImage?.extraLarge,
        bannerImage = bannerImage,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.TV,
        episodes = episodes,
        nextEpisode = null,
        genres = genres?.filterNotNull() ?: emptyList(),
        averageScore = averageScore,
        popularity = null,
        season = season?.name,
        year = seasonYear,
        studio = null
    )

    private fun GetMediaDetailsQuery.Media.toMangaDetails() = Manga(
        id = id.toString(),
        title = title?.english ?: title?.romaji ?: "Unknown",
        description = description,
        coverImage = coverImage?.extraLarge,
        bannerImage = bannerImage,
        rating = averageScore?.toDouble()?.div(10.0),
        status = MediaStatus.RELEASING,
        format = MediaFormat.MANGA,
        chapters = chapters,
        volumes = null,
        genres = genres?.filterNotNull() ?: emptyList(),
        averageScore = averageScore,
        popularity = null
    )
}
