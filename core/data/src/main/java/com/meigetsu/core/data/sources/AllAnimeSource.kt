package com.meigetsu.core.data.sources
import com.meigetsu.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class AllAnimeSource @Inject constructor(
    private val client: OkHttpClient
) : Source {
    override val name: String = "AllAnime"
    override val baseUrl: String = "https://allanime.to"
    override val type: MediaType = MediaType.ANIME
    override suspend fun search(query: String): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        // Simplified search logic
        emptyList()
    }
    override suspend fun getLatest(page: Int): List<SourceSearchResult> = emptyList()
    override suspend fun getEpisodes(id: String): List<SourceEpisode> = emptyList()
    override suspend fun getStreamUrl(episode: SourceEpisode): String = ""
    override suspend fun getChapters(id: String): List<SourceChapter> = emptyList()
    override suspend fun getPages(chapter: SourceChapter): List<String> = emptyList()
}
