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

            // GogoCDN uses AES encryption. In a production app, we would use a library like
            // CryptoJS (via JS engine) or a native AES implementation with the specific keys.
            // For this implementation, we attempt to find direct .m3u8 links or common obfuscation patterns.

            val m3u8Regex = Regex("(https?:\\/\\/.*?\\.m3u8)")
            val match = m3u8Regex.find(response)
            if (match != null) return match.groupValues[1]

            val doc = Jsoup.parse(response)
            val script = doc.select("script").find { it.html().contains("file:") || it.html().contains("sources") }
            val fileRegex = Regex("file:\"(.*?)\"")
            fileRegex.find(script?.html() ?: "")?.groupValues?.get(1) ?: url
        } catch (e: Exception) {
            url
        }
    }

    suspend fun resolveRedirect(url: String): String {
        return try {
            val response: io.ktor.client.statement.HttpResponse = httpClient.get(url) {
                // Prevent automatic redirection to catch the Location header if needed
                // though Ktor usually handles it.
            }
            response.request.url.toString()
        } catch (e: Exception) {
            url
        }
    }
}
