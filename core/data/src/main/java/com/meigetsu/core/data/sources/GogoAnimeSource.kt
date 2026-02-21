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
class GogoAnimeSource @Inject constructor(
    private val client: OkHttpClient
) : Source {
    override val name: String = "GogoAnime"
    override val baseUrl: String = "https://gogoanime3.co"
    override val type: MediaType = MediaType.ANIME

    override suspend fun search(query: String): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/search.html?keyword=$query"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".last_episodes ul.items li").map {
            SourceSearchResult(
                id = it.select("a").attr("href").replace("/category/", ""),
                title = it.select(".name a").attr("title"),
                image = it.select("img").attr("src")
            )
        }
    }

    override suspend fun getLatest(page: Int): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/?page=$page"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".last_episodes ul.items li").map {
            SourceSearchResult(
                id = it.select("a").attr("href").split("-episode-")[0].replace("/", ""),
                title = it.select(".name a").text(),
                image = it.select("img").attr("src")
            )
        }
    }

    override suspend fun getEpisodes(id: String): List<SourceEpisode> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/category/$id"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")

        val animeId = doc.select("#movie_id").attr("value")
        val alias = doc.select("#alias_anime").attr("value")
        val defaultEp = doc.select("#default_ep").attr("value")

        // GogoAnime often uses AJAX for episode list
        val epUrl = "https://ajax.gogocdn.net/ajax/load-list-episode?ep_start=0&ep_end=1000&id=$animeId&default_ep=$defaultEp&alias=$alias"
        val epRequest = Request.Builder().url(epUrl).build()
        val epResponse = client.newCall(epRequest).execute()
        val epDoc = Jsoup.parse(epResponse.body?.string() ?: "")

        epDoc.select("li").map {
            val num = it.select(".name").text().replace("EP ", "").toIntOrNull() ?: 0
            SourceEpisode(
                id = it.select("a").attr("href").trim(),
                number = num,
                title = "Episode $num",
                url = baseUrl + it.select("a").attr("href").trim()
            )
        }.reversed()
    }

    override suspend fun getStreamUrl(episode: SourceEpisode): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(episode.url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        val iframeUrl = "https:" + doc.select(".play-video iframe").attr("src")

        // Simple extraction for GogoCDN/Vidstreaming
        val iframeRequest = Request.Builder().url(iframeUrl).build()
        val iframeResponse = client.newCall(iframeRequest).execute()
        val iframeBody = iframeResponse.body?.string() ?: ""

        val m3u8Regex = Regex("(https?:\\/\\/.*?\\.m3u8)")
        m3u8Regex.find(iframeBody)?.groupValues?.get(1) ?: ""
    }

    override suspend fun getChapters(id: String): List<SourceChapter> = emptyList()
    override suspend fun getPages(chapter: SourceChapter): List<String> = emptyList()
}
