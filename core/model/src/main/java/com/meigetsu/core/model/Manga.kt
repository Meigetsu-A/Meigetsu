package com.meigetsu.core.model

data class Manga(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val coverImage: String?,
    override val bannerImage: String?,
    override val rating: Double?,
    override val status: MediaStatus,
    override val format: MediaFormat,
    val chapters: Int?,
    val volumes: Int?,
    override val genres: List<String>,
    override val averageScore: Int?,
    override val popularity: Int?,
    override val idMal: Int? = null,
    override val year: Int? = null,
    override val trailerUrl: String? = null
) : Media
