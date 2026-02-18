package com.meigetsu.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    fun getPrimaryColor(): Flow<Int>
    suspend fun setPrimaryColor(color: Int)

    fun getSecondaryColor(): Flow<Int>
    suspend fun setSecondaryColor(color: Int)

    fun getAccentColor(): Flow<Int>
    suspend fun setAccentColor(color: Int)

    fun getBackgroundColor(): Flow<Int>
    suspend fun setBackgroundColor(color: Int)

    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)

    fun getCornerRadius(): Flow<Int>
    suspend fun setCornerRadius(radius: Int)

    fun getCardStyle(): Flow<String>
    suspend fun setCardStyle(style: String)

    fun getButtonStyle(): Flow<String>
    suspend fun setButtonStyle(style: String)

    fun getFontFamily(): Flow<String>
    suspend fun setFontFamily(font: String)

    fun getAnimationSpeed(): Flow<Float>
    suspend fun setAnimationSpeed(speed: Float)

    fun isMangaAnimationsEnabled(): Flow<Boolean>
    suspend fun setMangaAnimationsEnabled(enabled: Boolean)

    fun isVideoAnimationsEnabled(): Flow<Boolean>
    suspend fun setVideoAnimationsEnabled(enabled: Boolean)

    // Playback Settings
    fun getDefaultPlaybackSpeed(): Flow<Float>
    suspend fun setDefaultPlaybackSpeed(speed: Float)

    fun getDefaultQuality(): Flow<String>
    suspend fun setDefaultQuality(quality: String)

    fun isAutoNextEnabled(): Flow<Boolean>
    suspend fun setAutoNextEnabled(enabled: Boolean)

    fun isSkipIntroAutoEnabled(): Flow<Boolean>
    suspend fun setSkipIntroAutoEnabled(enabled: Boolean)

    // Reader Settings
    fun getReadingMode(): Flow<String>
    suspend fun setReadingMode(mode: String)

    fun getPreloadPageCount(): Flow<Int>
    suspend fun setPreloadPageCount(count: Int)

    // App Settings
    fun getLibraryLayout(): Flow<String>
    suspend fun setLibraryLayout(layout: String)

    fun getIncognitoMode(): Flow<Boolean>
    suspend fun setIncognitoMode(enabled: Boolean)

    fun getAdultContent(): Flow<Boolean>
    suspend fun setAdultContent(enabled: Boolean)

    fun getBiometricEnabled(): Flow<Boolean>
    suspend fun setBiometricEnabled(enabled: Boolean)

    fun getAutoRefreshInterval(): Flow<Int>
    suspend fun setAutoRefreshInterval(minutes: Int)

    fun isDataSaverEnabled(): Flow<Boolean>
    suspend fun setDataSaverEnabled(enabled: Boolean)

    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    suspend fun exportBackup(): String
    suspend fun importBackup(json: String)
}
