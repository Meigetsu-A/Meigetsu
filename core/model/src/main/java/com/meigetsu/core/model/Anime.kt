package com.meigetsu.core.model

data class Anime(
    override val id: String,
    override val title: String,
    override val description: String?,
    override val coverImage: String?,
    override val bannerImage: String?,
    override val rating: Double?,
    override val status: MediaStatus,
    override val format: MediaFormat,
    val episodes: Int?,
    val nextEpisode: Int?,
    override val genres: List<String>,
    override val averageScore: Int?,
    override val popularity: Int?,
    val season: String?,
    override val year: Int?,
    val studio: String?,
    override val trailerUrl: String? = null,
    override val idMal: Int? = null,
    val characters: List<Character> = emptyList()
) : Media

enum class MediaStatus {
    FINISHED, RELEASING, NOT_YET_RELEASED, CANCELLED, HIATUS
}

enum class MediaFormat {
    TV, TV_SHORT, MOVIE, SPECIAL, OVA, ONA, MUSIC, MANGA, NOVEL, ONE_SHOT
}
