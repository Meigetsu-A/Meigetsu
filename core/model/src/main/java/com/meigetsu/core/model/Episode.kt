package com.meigetsu.core.model

data class Episode(
    val id: String,
    val animeId: String,
    val number: Int,
    val title: String?,
    val thumbnail: String?,
    val airDate: String?
)

data class Chapter(
    val id: String,
    val mangaId: String,
    val number: Double,
    val title: String?,
    val scanlator: String?
)
