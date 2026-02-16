package com.meigetsu.core.model

data class Anime(
    val id: String,
    val title: String,
    val description: String?,
    val coverImage: String?,
    val bannerImage: String?,
    val rating: Double?,
    val status: MediaStatus,
    val format: MediaFormat,
    val episodes: Int?,
    val nextEpisode: Int?,
    val genres: List<String>,
    val averageScore: Int?,
    val popularity: Int?,
    val season: String?,
    val year: Int?,
    val studio: String?,
    val trailerUrl: String? = null,
    val idMal: Int? = null,
    val characters: List<Character> = emptyList()
)

enum class MediaStatus {
    FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED, HIATUS
}

enum class MediaFormat {
    TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC, MANGA, NOVEL, ONE_SHOT
}
