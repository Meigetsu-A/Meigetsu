package com.meigetsu.core.data.repository

import com.apollographql.apollo.ApolloClient
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.MediaRepository
import com.meigetsu.core.model.*
import com.meigetsu.core.network.*
import com.meigetsu.core.network.type.MediaSort
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
            val response = apolloClient.query(GetMediaListQuery(
                type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME),
                sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.TRENDING_DESC))
            )).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnime()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchCharacters(query: String): Flow<Resource<List<Character>>> = flow {
        if (query.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(SearchMediaQuery(
                search = com.apollographql.apollo.api.Optional.present(query)
            )).execute()
            val charList = response.data?.Page?.characters?.filterNotNull()?.map {
                Character(
                    id = it.id.toString(),
                    name = it.name?.full ?: "Unknown",
                    image = it.image?.large,
                    description = it.description
                )
            } ?: emptyList()
            emit(Resource.Success(charList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getTrendingManga(): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaListQuery(
                type = com.apollographql.apollo.api.Optional.present(MediaType.MANGA),
                sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.TRENDING_DESC))
            )).execute()
            val mangaList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toManga()
            } ?: emptyList()
            emit(Resource.Success(mangaList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPopularAnime(): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaListQuery(
                type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME),
                sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.POPULARITY_DESC))
            )).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnime()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getPopularManga(): Flow<Resource<List<Manga>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaListQuery(
                type = com.apollographql.apollo.api.Optional.present(MediaType.MANGA),
                sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.POPULARITY_DESC))
            )).execute()
            val mangaList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toManga()
            } ?: emptyList()
            emit(Resource.Success(mangaList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun getRecommendedAnime(): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMediaListQuery(
                type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME),
                sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.SCORE_DESC))
            )).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnime()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override fun searchAnime(query: String, page: Int): Flow<Resource<List<Anime>>> = flow {
        if (query.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }
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
        if (query.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }
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
            val intId = id.toIntOrNull() ?: run {
                emit(Resource.Error("Invalid ID format"))
                return@flow
            }
            val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(intId))).execute()
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
            val intId = id.toIntOrNull() ?: run {
                emit(Resource.Error("Invalid ID format"))
                return@flow
            }
            val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(intId))).execute()
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
            val intId = id.toIntOrNull() ?: run {
                emit(Resource.Error("Invalid ID format"))
                return@flow
            }
            val response = apolloClient.query(GetCharacterDetailsQuery(id = com.apollographql.apollo.api.Optional.present(intId))).execute()
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

    override fun getMultipleAnime(ids: List<String>): Flow<Resource<List<Anime>>> = flow {
        if (ids.isEmpty()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }
        emit(Resource.Loading())
        try {
            val response = apolloClient.query(GetMultipleMediaQuery(
                ids = com.apollographql.apollo.api.Optional.present(ids.mapNotNull { it.toIntOrNull() }),
                type = com.apollographql.apollo.api.Optional.present(MediaType.ANIME)
            )).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.toAnimeMultiple()
            } ?: emptyList()
            emit(Resource.Success(animeList))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    private fun GetMediaListQuery.Medium.toAnime() = Anime(
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
        popularity = popularity,
        season = season?.name,
        year = seasonYear,
        studio = null,
        trailerUrl = trailer?.let {
            if (it.site == "youtube") "https://www.youtube.com/watch?v=${it.id}" else null
        }
    )

    private fun GetMediaListQuery.Medium.toManga() = Manga(
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
        popularity = popularity
    )

    private fun SearchMediaQuery.Medium.toAnimeSearch() = Anime(
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
        popularity = popularity,
        season = season?.name,
        year = seasonYear,
        studio = null,
        trailerUrl = trailer?.let {
            if (it.site == "youtube") "https://www.youtube.com/watch?v=${it.id}" else null
        }
    )

    private fun SearchMediaQuery.Medium.toMangaSearch() = Manga(
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
        popularity = popularity
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
        popularity = popularity,
        season = season?.name,
        year = seasonYear,
        studio = null,
        trailerUrl = null
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
        popularity = popularity
    )

    private fun GetMultipleMediaQuery.Medium.toAnimeMultiple() = Anime(
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
        popularity = popularity,
        season = season?.name,
        year = seasonYear,
        studio = null
    )
}
