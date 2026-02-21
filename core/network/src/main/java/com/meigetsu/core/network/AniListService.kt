package com.meigetsu.core.network
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.meigetsu.core.model.Media
import com.meigetsu.core.model.MediaStatus
import com.meigetsu.core.model.MediaType
import com.meigetsu.core.network.type.*
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AniListService @Inject constructor(
    private val apolloClient: ApolloClient
) {
    suspend fun getTrending(type: MediaType): List<Media> {
        return getMediaList(type, listOf(MediaSort.TRENDING_DESC))
    }
    suspend fun getPopular(type: MediaType): List<Media> {
        return getMediaList(type, listOf(MediaSort.POPULARITY_DESC))
    }
    suspend fun getUpcoming(type: MediaType): List<Media> {
        return getMediaList(type, listOf(MediaSort.START_DATE_DESC))
    }
    private suspend fun getMediaList(type: MediaType, sort: List<MediaSort>): List<Media> {
        val response = apolloClient.query(GetMediaListQuery(
            type = Optional.present(type.toApolloType()),
            sort = Optional.present(sort)
        )).execute()
        return response.data?.Page?.media?.filterNotNull()?.map { it.toMedia(type) } ?: emptyList()
    }
    suspend fun search(query: String, type: MediaType): List<Media> {
        val response = apolloClient.query(SearchMediaQuery(
            search = Optional.present(query),
            type = Optional.present(type.toApolloType())
        )).execute()
        return response.data?.Page?.media?.filterNotNull()?.map { it.toMediaSearch(type) } ?: emptyList()
    }
    suspend fun getDetails(id: String, type: MediaType): Media? {
        val response = apolloClient.query(GetMediaDetailsQuery(
            id = Optional.present(id.toInt())
        )).execute()
        return response.data?.Media?.toMediaDetails(type)
    }
}
private fun MediaType.toApolloType(): com.meigetsu.core.network.type.MediaType {
    return when(this) {
        MediaType.ANIME -> com.meigetsu.core.network.type.MediaType.ANIME
        else -> com.meigetsu.core.network.type.MediaType.MANGA
    }
}
private fun GetMediaListQuery.Medium.toMedia(type: MediaType) = Media(
    id = id.toString(),
    title = title?.english ?: title?.romaji ?: "Unknown",
    description = description,
    coverImage = coverImage?.extraLarge,
    bannerImage = bannerImage,
    type = type,
    status = status?.toMediaStatus() ?: MediaStatus.ONGOING,
    score = averageScore,
    genres = genres?.filterNotNull() ?: emptyList(),
    episodeCount = episodes,
    chapterCount = chapters,
    year = seasonYear
)
private fun SearchMediaQuery.Medium.toMediaSearch(type: MediaType) = Media(
    id = id.toString(),
    title = title?.english ?: title?.romaji ?: "Unknown",
    description = description,
    coverImage = coverImage?.extraLarge,
    bannerImage = bannerImage,
    type = type,
    status = status?.toMediaStatus() ?: MediaStatus.ONGOING,
    score = averageScore,
    genres = genres?.filterNotNull() ?: emptyList(),
    episodeCount = episodes,
    chapterCount = chapters,
    year = seasonYear
)
private fun GetMediaDetailsQuery.Media.toMediaDetails(type: MediaType) = Media(
    id = id.toString(),
    title = title?.english ?: title?.romaji ?: "Unknown",
    description = description,
    coverImage = coverImage?.extraLarge,
    bannerImage = bannerImage,
    type = type,
    status = status?.toMediaStatus() ?: MediaStatus.ONGOING,
    score = averageScore,
    genres = genres?.filterNotNull() ?: emptyList(),
    episodeCount = episodes,
    chapterCount = chapters,
    year = seasonYear
)
private fun com.meigetsu.core.network.type.MediaStatus.toMediaStatus(): MediaStatus {
    return when(this) {
        com.meigetsu.core.network.type.MediaStatus.FINISHED -> MediaStatus.COMPLETED
        else -> MediaStatus.ONGOING
    }
}
