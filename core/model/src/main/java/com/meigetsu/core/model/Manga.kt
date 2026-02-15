package com.meigetsu.core.model

data class Manga(
    val id: String,
    val title: String,
    val description: String?,
    val coverImage: String?,
    val bannerImage: String?,
    val rating: Double?,
    val status: MediaStatus,
    val format: MediaFormat,
    val chapters: Int?,
    val volumes: Int?,
    val genres: List<String>,
    val averageScore: Int?,
    val popularity: Int?,
    val idMal: Int? = null
)
