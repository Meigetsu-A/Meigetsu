package com.meigetsu.core.data.repository

import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.EverythingMoeRepository
import com.meigetsu.core.model.ExternalSource
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EverythingMoeRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient
) : EverythingMoeRepository {

    override fun getSources(): Flow<Resource<List<ExternalSource>>> = flow {
        emit(Resource.Loading())
        try {
            val response = httpClient.get("https://everythingmoe.com/").bodyAsText()
            val doc = Jsoup.parse(response)
            val sources = mutableListOf<ExternalSource>()

            // EverythingMoe uses <h3> for categories and following elements for sites
            val categories = doc.select("h3")
            for (categoryElement in categories) {
                val categoryName = categoryElement.text().replace(Regex("\\(\\d+\\)"), "").trim()

                // The sites are in a container following the <h3> or inside it?
                // Looking at the view_text_website output, it seems they are listed.
                // In the HTML, they are often inside a <div> or <ul> following the header.

                // Actually, EverythingMoe uses a specific structure.
                // Let's look for elements with class that might represent a site.
                // From experience, they are often links with icons.

                val parent = categoryElement.parent()
                val siteElements = parent?.select("a[href^='/s/']") ?: emptyList()

                for (site in siteElements) {
                    var name = site.text().trim()
                    if (name.isEmpty()) continue

                    // Clean name from tags like MULT, DONGHUA, etc.
                    name = name.replace(Regex("\\b(MULT|DONGHUA|MANHWA|MANGA|DDL|HUB|LOGIN|PHYSICAL|USENET|XDCC|TORRENT)\\b"), "").trim()

                    val href = site.attr("href")
                    val fullUrl = if (href.startsWith("http")) href else "https://everythingmoe.com$href"

                    // Try to find icon
                    val icon = site.parent()?.selectFirst("img")?.attr("src")
                    val fullIconUrl = if (icon?.startsWith("http") == false) "https://static.everythingmoe.com$icon" else icon

                    sources.add(ExternalSource(
                        name = name,
                        url = fullUrl,
                        iconUrl = fullIconUrl,
                        category = categoryName
                    ))
                }
            }

            if (sources.isEmpty()) {
                // Fallback: search for list items
                val items = doc.select("li")
                for (item in items) {
                    val link = item.selectFirst("a[href^='/s/']") ?: continue
                    val name = link.text().trim()
                    val href = link.attr("href")
                    sources.add(ExternalSource(
                        name = name,
                        url = "https://everythingmoe.com$href",
                        iconUrl = null,
                        category = "General"
                    ))
                }
            }

            emit(Resource.Success(sources.distinctBy { it.url }))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to scrape Everything Moe"))
        }
    }
}
