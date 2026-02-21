package com.meigetsu.core.data.sources
import com.meigetsu.core.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class MangaDexSource @Inject constructor(
    private val client: OkHttpClient
) : Source {
    override val name: String = "MangaDex"
    override val baseUrl: String = "https://api.mangadex.org"
    override val type: MediaType = MediaType.MANGA
    override suspend fun search(query: String): List<SourceSearchResult> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/manga?title=$query&limit=20&includes[]=cover_art"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "{}")
        val data = json.getJSONArray("data")
        val results = mutableListOf<SourceSearchResult>()
        for (i in 0 until data.length()) {
            val manga = data.getJSONObject(i)
            val attributes = manga.getJSONObject("attributes")
            val id = manga.getString("id")
            var fileName = ""
            val relationships = manga.getJSONArray("relationships")
            for (j in 0 until relationships.length()) {
                val rel = relationships.getJSONObject(j)
                if (rel.getString("type") == "cover_art") {
                    fileName = rel.getJSONObject("attributes").getString("fileName")
                }
            }
            results.add(SourceSearchResult(
                id = id,
                title = attributes.getJSONObject("title").optString("en", "Unknown"),
                image = "https://uploads.mangadex.org/covers/$id/$fileName"
            ))
        }
        results
    }
    override suspend fun getLatest(page: Int): List<SourceSearchResult> = emptyList()
    override suspend fun getEpisodes(id: String): List<SourceEpisode> = emptyList()
    override suspend fun getStreamUrl(episode: SourceEpisode): String = ""
    override suspend fun getChapters(id: String): List<SourceChapter> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/manga/$id/feed?translatedLanguage[]=en&order[chapter]=desc&limit=100"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "{}")
        val data = json.getJSONArray("data")
        val results = mutableListOf<SourceChapter>()
        for (i in 0 until data.length()) {
            val chapter = data.getJSONObject(i)
            val attributes = chapter.getJSONObject("attributes")
            results.add(SourceChapter(
                id = chapter.getString("id"),
                number = attributes.optString("chapter", "0").toFloatOrNull() ?: 0f,
                title = attributes.optString("title", "Chapter ${attributes.optString("chapter")}"),
                url = ""
            ))
        }
        results
    }
    override suspend fun getPages(chapter: SourceChapter): List<String> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/at-home/server/${chapter.id}"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = JSONObject(response.body?.string() ?: "{}")
        val baseUrl = json.getString("baseUrl")
        val chapterData = json.getJSONObject("chapter")
        val hash = chapterData.getString("hash")
        val data = chapterData.getJSONArray("data")
        val results = mutableListOf<String>()
        for (i in 0 until data.length()) {
            results.add("$baseUrl/data/$hash/${data.getString(i)}")
        }
        results
    }
}
