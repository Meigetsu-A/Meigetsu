package com.meigetsu.core.model

data class Character(
    val id: String,
    val name: String,
    val image: String?,
    val description: String?,
    val associatedMedia: List<Anime> = emptyList()
)
