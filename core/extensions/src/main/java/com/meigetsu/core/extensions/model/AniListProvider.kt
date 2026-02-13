package com.meigetsu.core.extensions.model

import com.apollographql.apollo.ApolloClient
import com.meigetsu.core.extensions.*
import com.meigetsu.core.model.*
import com.meigetsu.core.network.*
import com.meigetsu.core.network.type.MediaType
import com.meigetsu.core.network.type.MediaSort
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AniListProvider @Inject constructor(
    private val apolloClient: ApolloClient
) : AnimeProvider, MangaProvider {

    override val metadata = ExtensionMetadata(
        id = "anilist",
        name = "AniList (Built-in)",
        version = "1.0.0",
        description = "Official AniList metadata provider",
        iconUrl = "https://anilist.co/img/icons/android-chrome-512x512.png",
        type = ExtensionType.BOTH,
        pkgName = "com.meigetsu.extension.anilist",
        author = "Meigetsu",
        isTrusted = true
    )

    override suspend fun getEpisodes(animeId: String): List<Episode> {
        val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(animeId.toInt()))).execute()
        val media = response.data?.Media ?: return emptyList()
        val total = media.episodes ?: 0
        return (1..total).map {
            Episode(
                id = "${animeId}_$it",
                animeId = animeId,
                number = it,
                title = "Episode $it",
                airDate = null,
                thumbnail = null
            )
        }
    }

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        // AniList doesn't provide streams, this is a metadata provider.
        // In a real app, this might return links to other sites or a "Search on Google" link
        return emptyList()
    }

    override suspend fun getChapters(mangaId: String): List<Chapter> {
        val response = apolloClient.query(GetMediaDetailsQuery(id = com.apollographql.apollo.api.Optional.present(mangaId.toInt()))).execute()
        val media = response.data?.Media ?: return emptyList()
        val total = media.chapters ?: 0
        return (1..total).map {
            Chapter(
                id = "${mangaId}_$it",
                mangaId = mangaId,
                number = it.toDouble(),
                title = "Chapter $it",
                scanlator = null
            )
        }
    }

    override suspend fun getPages(chapter: Chapter): List<String> = emptyList()

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        val response = apolloClient.query(SearchMediaQuery(
            search = com.apollographql.apollo.api.Optional.present(query),
            page = com.apollographql.apollo.api.Optional.present(page)
        )).execute()
        return response.data?.Page?.media?.filterNotNull()?.map {
            MediaSearchResult(
                id = it.id.toString(),
                title = it.title?.english ?: it.title?.romaji ?: "Unknown",
                imageUrl = it.coverImage?.extraLarge,
                type = it.format?.name ?: "UNKNOWN",
                extensionId = metadata.id
            )
        } ?: emptyList()
    }

    override suspend fun getPopular(page: Int): List<MediaSearchResult> {
        val response = apolloClient.query(GetMediaListQuery(
            sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.POPULARITY_DESC)),
            page = com.apollographql.apollo.api.Optional.present(page)
        )).execute()
        return response.data?.Page?.media?.filterNotNull()?.map {
            MediaSearchResult(
                id = it.id.toString(),
                title = it.title?.english ?: it.title?.romaji ?: "Unknown",
                imageUrl = it.coverImage?.extraLarge,
                type = it.format?.name ?: "UNKNOWN",
                extensionId = metadata.id
            )
        } ?: emptyList()
    }

    override suspend fun getLatest(page: Int): List<MediaSearchResult> {
        val response = apolloClient.query(GetMediaListQuery(
            sort = com.apollographql.apollo.api.Optional.present(listOf(MediaSort.START_DATE_DESC)),
            page = com.apollographql.apollo.api.Optional.present(page)
        )).execute()
        return response.data?.Page?.media?.filterNotNull()?.map {
            MediaSearchResult(
                id = it.id.toString(),
                title = it.title?.english ?: it.title?.romaji ?: "Unknown",
                imageUrl = it.coverImage?.extraLarge,
                type = it.format?.name ?: "UNKNOWN",
                extensionId = metadata.id
            )
        } ?: emptyList()
    }
}
