package com.meigetsu.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.meigetsu.core.domain.repository.PreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceRepository {

    private val primaryColorKey = intPreferencesKey("primary_color")
    private val secondaryColorKey = intPreferencesKey("secondary_color")
    private val accentColorKey = intPreferencesKey("accent_color")
    private val backgroundColorKey = intPreferencesKey("background_color")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val cornerRadiusKey = intPreferencesKey("corner_radius")
    private val cardStyleKey = stringPreferencesKey("card_style")
    private val buttonStyleKey = stringPreferencesKey("button_style")
    private val fontFamilyKey = stringPreferencesKey("font_family")
    private val animationSpeedKey = floatPreferencesKey("animation_speed")
    private val mangaAnimationsKey = booleanPreferencesKey("manga_animations")
    private val videoAnimationsKey = booleanPreferencesKey("video_animations")

    private val defaultPlaybackSpeedKey = floatPreferencesKey("default_playback_speed")
    private val defaultQualityKey = stringPreferencesKey("default_quality")
    private val autoNextKey = booleanPreferencesKey("auto_next")
    private val skipIntroAutoKey = booleanPreferencesKey("skip_intro_auto")

    private val readingModeKey = stringPreferencesKey("reading_mode")
    private val preloadPageCountKey = intPreferencesKey("preload_page_count")

    private val libraryLayoutKey = stringPreferencesKey("library_layout")
    private val incognitoKey = booleanPreferencesKey("incognito_mode")
    private val adultContentKey = booleanPreferencesKey("adult_content")
    private val biometricEnabledKey = booleanPreferencesKey("biometric_enabled")
    private val autoRefreshIntervalKey = intPreferencesKey("auto_refresh_interval")
    private val dataSaverKey = booleanPreferencesKey("data_saver")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    override fun getPrimaryColor(): Flow<Int> = context.dataStore.data.map { it[primaryColorKey] ?: 0xFFE50914.toInt() }
    override suspend fun setPrimaryColor(color: Int) { context.dataStore.edit { it[primaryColorKey] = color } }

    override fun getSecondaryColor(): Flow<Int> = context.dataStore.data.map { it[secondaryColorKey] ?: 0xFF141414.toInt() }
    override suspend fun setSecondaryColor(color: Int) { context.dataStore.edit { it[secondaryColorKey] = color } }

    override fun getAccentColor(): Flow<Int> = context.dataStore.data.map { it[accentColorKey] ?: 0xFFB9090B.toInt() }
    override suspend fun setAccentColor(color: Int) { context.dataStore.edit { it[accentColorKey] = color } }

    override fun getBackgroundColor(): Flow<Int> = context.dataStore.data.map { it[backgroundColorKey] ?: 0xFF000000.toInt() }
    override suspend fun setBackgroundColor(color: Int) { context.dataStore.edit { it[backgroundColorKey] = color } }

    override fun getThemeMode(): Flow<String> = context.dataStore.data.map { it[themeModeKey] ?: "SYSTEM" }
    override suspend fun setThemeMode(mode: String) { context.dataStore.edit { it[themeModeKey] = mode } }

    override fun getCornerRadius(): Flow<Int> = context.dataStore.data.map { it[cornerRadiusKey] ?: 12 }
    override suspend fun setCornerRadius(radius: Int) { context.dataStore.edit { it[cornerRadiusKey] = radius } }

    override fun getCardStyle(): Flow<String> = context.dataStore.data.map { it[cardStyleKey] ?: "DEFAULT" }
    override suspend fun setCardStyle(style: String) { context.dataStore.edit { it[cardStyleKey] = style } }

    override fun getButtonStyle(): Flow<String> = context.dataStore.data.map { it[buttonStyleKey] ?: "ROUNDED" }
    override suspend fun setButtonStyle(style: String) { context.dataStore.edit { it[buttonStyleKey] = style } }

    override fun getFontFamily(): Flow<String> = context.dataStore.data.map { it[fontFamilyKey] ?: "DEFAULT" }
    override suspend fun setFontFamily(font: String) { context.dataStore.edit { it[fontFamilyKey] = font } }

    override fun getAnimationSpeed(): Flow<Float> = context.dataStore.data.map { it[animationSpeedKey] ?: 1.0f }
    override suspend fun setAnimationSpeed(speed: Float) { context.dataStore.edit { it[animationSpeedKey] = speed } }

    override fun isMangaAnimationsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[mangaAnimationsKey] ?: true }
    override suspend fun setMangaAnimationsEnabled(enabled: Boolean) { context.dataStore.edit { it[mangaAnimationsKey] = enabled } }

    override fun isVideoAnimationsEnabled(): Flow<Boolean> = context.dataStore.data.map { it[videoAnimationsKey] ?: true }
    override suspend fun setVideoAnimationsEnabled(enabled: Boolean) { context.dataStore.edit { it[videoAnimationsKey] = enabled } }

    override fun getDefaultPlaybackSpeed(): Flow<Float> = context.dataStore.data.map { it[defaultPlaybackSpeedKey] ?: 1.0f }
    override suspend fun setDefaultPlaybackSpeed(speed: Float) { context.dataStore.edit { it[defaultPlaybackSpeedKey] = speed } }

    override fun getDefaultQuality(): Flow<String> = context.dataStore.data.map { it[defaultQualityKey] ?: "1080p" }
    override suspend fun setDefaultQuality(quality: String) { context.dataStore.edit { it[defaultQualityKey] = quality } }

    override fun isAutoNextEnabled(): Flow<Boolean> = context.dataStore.data.map { it[autoNextKey] ?: true }
    override suspend fun setAutoNextEnabled(enabled: Boolean) { context.dataStore.edit { it[autoNextKey] = enabled } }

    override fun isSkipIntroAutoEnabled(): Flow<Boolean> = context.dataStore.data.map { it[skipIntroAutoKey] ?: false }
    override suspend fun setSkipIntroAutoEnabled(enabled: Boolean) { context.dataStore.edit { it[skipIntroAutoKey] = enabled } }

    override fun getReadingMode(): Flow<String> = context.dataStore.data.map { it[readingModeKey] ?: "VERTICAL" }
    override suspend fun setReadingMode(mode: String) { context.dataStore.edit { it[readingModeKey] = mode } }

    override fun getPreloadPageCount(): Flow<Int> = context.dataStore.data.map { it[preloadPageCountKey] ?: 5 }
    override suspend fun setPreloadPageCount(count: Int) { context.dataStore.edit { it[preloadPageCountKey] = count } }

    override fun getLibraryLayout(): Flow<String> = context.dataStore.data.map { it[libraryLayoutKey] ?: "GRID" }
    override suspend fun setLibraryLayout(layout: String) { context.dataStore.edit { it[libraryLayoutKey] = layout } }

    override fun getIncognitoMode(): Flow<Boolean> = context.dataStore.data.map { it[incognitoKey] ?: false }
    override suspend fun setIncognitoMode(enabled: Boolean) { context.dataStore.edit { it[incognitoKey] = enabled } }

    override fun getAdultContent(): Flow<Boolean> = context.dataStore.data.map { it[adultContentKey] ?: false }
    override suspend fun setAdultContent(enabled: Boolean) { context.dataStore.edit { it[adultContentKey] = enabled } }

    override fun getBiometricEnabled(): Flow<Boolean> = context.dataStore.data.map { it[biometricEnabledKey] ?: false }
    override suspend fun setBiometricEnabled(enabled: Boolean) { context.dataStore.edit { it[biometricEnabledKey] = enabled } }

    override fun getAutoRefreshInterval(): Flow<Int> = context.dataStore.data.map { it[autoRefreshIntervalKey] ?: 0 }
    override suspend fun setAutoRefreshInterval(minutes: Int) { context.dataStore.edit { it[autoRefreshIntervalKey] = minutes } }

    override fun isDataSaverEnabled(): Flow<Boolean> = context.dataStore.data.map { it[dataSaverKey] ?: false }
    override suspend fun setDataSaverEnabled(enabled: Boolean) { context.dataStore.edit { it[dataSaverKey] = enabled } }

    override fun isOnboardingCompleted(): Flow<Boolean> = context.dataStore.data.map { it[onboardingCompletedKey] ?: false }
    override suspend fun setOnboardingCompleted(completed: Boolean) { context.dataStore.edit { it[onboardingCompletedKey] = completed } }

    override suspend fun exportBackup(): String {
        return "{ \"version\": 1, \"settings\": {} }"
    }

    override suspend fun importBackup(json: String) {
        // Parse and apply settings
    }
}
