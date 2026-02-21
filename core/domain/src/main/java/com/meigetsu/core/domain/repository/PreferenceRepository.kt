package com.meigetsu.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getPrimaryColor(): Flow<Long>
    suspend fun setPrimaryColor(color: Long)

    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)

    fun getCornerRadius(): Flow<Int>
    suspend fun setCornerRadius(radius: Int)

    fun getBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)

    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}
