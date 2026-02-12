package com.meigetsu.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getPrimaryColor(): Flow<Int>
    suspend fun setPrimaryColor(color: Int)

    fun getCornerRadius(): Flow<Int>
    suspend fun setCornerRadius(radius: Int)

    fun getReadingMode(): Flow<String>
    suspend fun setReadingMode(mode: String)
}
