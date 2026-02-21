package com.meigetsu.core.model

data class History(
    val mediaId: String,
    val lastItemNumber: Double,
    val position: Long,
    val timestamp: Long
)
