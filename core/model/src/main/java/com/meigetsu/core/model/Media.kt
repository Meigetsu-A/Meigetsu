package com.meigetsu.core.model

data class Media(
    val id: String, // AniList ID
    val title: String,
    val description: String?,
    val coverImage: String?,
    val bannerImage: String?,
    val type: MediaType,
    val status: MediaStatus,
    val score: Int?,
    val genres: List<String>,
    val episodeCount: Int? = null,
    val chapterCount: Int? = null,
    val year: Int? = null
)
