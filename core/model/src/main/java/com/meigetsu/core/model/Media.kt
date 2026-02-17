package com.meigetsu.core.model

interface Media {
    val id: String
    val title: String
    val description: String?
    val coverImage: String?
    val bannerImage: String?
    val rating: Double?
    val status: MediaStatus
    val format: MediaFormat
    val genres: List<String>
    val averageScore: Int?
    val popularity: Int?
    val year: Int?
    val trailerUrl: String?
    val idMal: Int?
}
