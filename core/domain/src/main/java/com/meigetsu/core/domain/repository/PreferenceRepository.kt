package com.meigetsu.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getPrimaryColor(): Flow<Int>
    suspend fun setPrimaryColor(color: Int)

    fun getCornerRadius(): Flow<Int>
    suspend fun setCornerRadius(radius: Int)

    fun getReadingMode(): Flow<String>
    suspend fun setReadingMode(mode: String)

    fun getLibraryLayout(): Flow<String>
    suspend fun setLibraryLayout(layout: String)

    fun getIncognitoMode(): Flow<Boolean>
    suspend fun setIncognitoMode(enabled: Boolean)

    fun getAdultContent(): Flow<Boolean>
    suspend fun setAdultContent(enabled: Boolean)

    fun getBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)

    suspend fun exportBackup(): String
    suspend fun importBackup(json: String)
}
