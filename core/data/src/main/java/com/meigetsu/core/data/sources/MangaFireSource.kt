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
class MangaFireSource @Inject constructor(
    private val client: OkHttpClient
) : Source {
    override val name: String = "MangaFire"
    override val baseUrl: String = "https://mangafire.to"
    override val type: MediaType = MediaType.MANGA

    override suspend fun search(query: String): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/filter?keyword=$query"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".original .unit").map {
            SourceSearchResult(
                id = it.select("a").attr("href").replace("/manga/", ""),
                title = it.select(".inner .info h3 a").attr("title"),
                image = it.select("img").attr("src")
            )
        }
    }

    override suspend fun getLatest(page: Int): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/filter?sort=latest&page=$page"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".original .unit").map {
            SourceSearchResult(
                id = it.select("a").attr("href").replace("/manga/", ""),
                title = it.select(".inner .info h3 a").attr("title"),
                image = it.select("img").attr("src")
            )
        }
    }

    override suspend fun getChapters(id: String): List<SourceChapter> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/manga/$id"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".chapters li").map {
            SourceChapter(
                id = it.select("a").attr("href").replace("/read/", ""),
                number = it.select("a").attr("data-number").toDoubleOrNull() ?: 0.0,
                title = it.select("a span").text(),
                url = baseUrl + it.select("a").attr("href")
            )
        }
    }

    override suspend fun getPages(chapter: SourceChapter): List<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(chapter.url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        // MangaFire often uses JS to load pages. Simplified extraction:
        doc.select(".reader-area img").map { it.attr("src") }
    }

    override suspend fun getEpisodes(id: String): List<SourceEpisode> = emptyList()
    override suspend fun getStreamUrl(episode: SourceEpisode): String = ""
}
