package com.meigetsu.core.data.repository

import android.util.Xml
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.NewsRepository
import com.meigetsu.core.model.NewsArticle
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val client: HttpClient
) : NewsRepository {
    override fun getLatestNews(): Flow<Resource<List<NewsArticle>>> = flow {
        emit(Resource.Loading())
        try {
            val response = client.get("https://www.animenewsnetwork.com/all/rss.xml").bodyAsText()
            val articles = parseRss(response)
            emit(Resource.Success(articles))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to fetch news"))
        }
    }

    private fun parseRss(xml: String): List<NewsArticle> {
        val articles = mutableListOf<NewsArticle>()
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(StringReader(xml))

        var eventType = parser.eventType
        var currentArticle: MutableNewsArticle? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (name == "item") {
                        currentArticle = MutableNewsArticle()
                    } else if (currentArticle != null) {
                        when (name) {
                            "title" -> currentArticle.title = parser.nextText()
                            "link" -> currentArticle.link = parser.nextText()
                            "description" -> currentArticle.description = parser.nextText()
                            "pubDate" -> currentArticle.pubDate = parser.nextText()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (name == "item" && currentArticle != null) {
                        articles.add(currentArticle.toNewsArticle())
                        currentArticle = null
                    }
                }
            }
            eventType = parser.next()
        }
        return articles
    }

    private class MutableNewsArticle {
        var title: String = ""
        var link: String = ""
        var description: String = ""
        var pubDate: String = ""

        fun toNewsArticle() = NewsArticle(
            title = title,
            description = description.replace(Regex("<[^>]*>"), ""), // Simple HTML strip
            url = link,
            imageUrl = null, // RSS doesn't always have image in standard tags
            date = pubDate,
            source = "Anime News Network"
        )
    }
}
