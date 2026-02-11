package com.meigetsu.core.model

data class Character(
    val id: String,
    val name: String,
    val image: String?,
    val description: String?,
    val voiceActors: List<VoiceActor> = emptyList()
)

data class VoiceActor(
    val id: String,
    val name: String,
    val image: String?,
    val language: String?
)
