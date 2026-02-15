package com.meigetsu.core.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JikanService @Inject constructor(
    private val client: HttpClient
) {
    private val apiBase = "https://api.jikan.moe/v4"

    suspend fun searchCharacters(query: String, page: Int = 1): JikanCharacterResponse {
        return client.get("$apiBase/characters") {
            parameter("q", query)
            parameter("page", page)
            parameter("limit", 20)
        }.body()
    }

    suspend fun getSchedules(filter: String? = null): JikanScheduleResponse {
        return client.get("$apiBase/schedules") {
            if (filter != null) parameter("filter", filter)
        }.body()
    }
}

@Serializable
data class JikanCharacterResponse(
    val data: List<JikanCharacterData>,
    val pagination: JikanPagination
)

@Serializable
data class JikanCharacterData(
    val mal_id: Int,
    val name: String,
    val images: JikanImages,
    val about: String? = null
)

@Serializable
data class JikanScheduleResponse(
    val data: List<JikanAnimeData>
)

@Serializable
data class JikanAnimeData(
    val mal_id: Int,
    val title: String,
    val images: JikanImages,
    val broadcast: JikanBroadcast? = null
)

@Serializable
data class JikanImages(
    val jpg: JikanJpg
)

@Serializable
data class JikanJpg(
    val image_url: String
)

@Serializable
data class JikanPagination(
    val last_visible_page: Int,
    val has_next_page: Boolean
)

@Serializable
data class JikanBroadcast(
    val day: String? = null,
    val time: String? = null,
    val timezone: String? = null,
    val string: String? = null
)
