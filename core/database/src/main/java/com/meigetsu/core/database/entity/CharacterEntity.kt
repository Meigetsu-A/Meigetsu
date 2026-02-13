package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_characters")
data class CharacterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val image: String?,
    val description: String?,
    val dateAdded: Long = System.currentTimeMillis()
)
