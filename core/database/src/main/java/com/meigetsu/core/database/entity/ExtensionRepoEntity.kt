package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extension_repos")
data class ExtensionRepoEntity(
    @PrimaryKey
    val url: String,
    val name: String,
    val lastUpdated: Long,
    val isTrusted: Boolean = false
)
