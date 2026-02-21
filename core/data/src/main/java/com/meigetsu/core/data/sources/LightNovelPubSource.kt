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
class LightNovelPubSource @Inject constructor(
    private val client: OkHttpClient
) : Source {
    override val name: String = "LightNovelPub"
    override val baseUrl: String = "https://www.lightnovelpub.com"
    override val type: MediaType = MediaType.NOVEL
    override suspend fun search(query: String): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/search?keyword=$query"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".novel-list .novel-item").map {
            SourceSearchResult(
                id = it.select("a").attr("href").replace("/novel/", ""),
                title = it.select(".novel-title").text(),
                image = it.select("img").attr("data-src")
            )
        }
    }
    override suspend fun getLatest(page: Int): List<SourceSearchResult> = emptyList()
    override suspend fun getEpisodes(id: String): List<SourceEpisode> = emptyList()
    override suspend fun getStreamUrl(episode: SourceEpisode): String = ""
    override suspend fun getChapters(id: String): List<SourceChapter> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/novel/$id/chapters"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        doc.select(".chapter-list li a").map {
            SourceChapter(
                id = it.attr("href").replace("/novel/$id/", ""),
                number = it.select(".chapter-no").text().replace("Ch.", "").trim().toFloatOrNull() ?: 0f,
                title = it.select(".chapter-title").text(),
                url = baseUrl + it.attr("href")
            )
        }
    }
    override suspend fun getPages(chapter: SourceChapter): List<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(chapter.url).build()
        val response = client.newCall(request).execute()
        val doc = Jsoup.parse(response.body?.string() ?: "")
        val content = doc.select("#chapter-container").html()
        listOf("text://$content")
    }
}
