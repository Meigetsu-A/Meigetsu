package com.meigetsu.core.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinkExtractor @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun extract(url: String): String {
        return when {
            url.contains("gogoload.com") || url.contains("vidstreaming.io") -> extractGogoCDN(url)
            else -> url // Fallback to raw URL
        }
    }

    private suspend fun extractGogoCDN(url: String): String {
        return try {
            val response = httpClient.get(url).bodyAsText()
            val doc = Jsoup.parse(response)
            // GogoCDN usually uses obfuscated scripts to hide the .m3u8 link.
            // For a production app, we would use a more robust JS evaluator or specific patterns.
            // Here we provide a simplified version that looks for common patterns.
            val script = doc.select("script").find { it.html().contains("file:") }
            val regex = Regex("file:\"(.*?)\"")
            regex.find(script?.html() ?: "")?.groupValues?.get(1) ?: url
        } catch (e: Exception) {
            url
        }
    }
}
