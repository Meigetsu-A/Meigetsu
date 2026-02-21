package com.meigetsu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val mediaId: String,
    val lastItemNumber: Double,
    val position: Long,
    val timestamp: Long
)
