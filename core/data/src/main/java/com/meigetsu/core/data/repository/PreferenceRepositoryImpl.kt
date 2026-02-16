package com.meigetsu.core.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val cornerRadiusKey = intPreferencesKey("corner_radius")
    private val readingModeKey = stringPreferencesKey("reading_mode")
    private val libraryLayoutKey = stringPreferencesKey("library_layout")
    private val incognitoKey = booleanPreferencesKey("incognito_mode")
    private val adultContentKey = booleanPreferencesKey("adult_content")
    private val biometricEnabledKey = booleanPreferencesKey("biometric_enabled")
    private val autoRefreshIntervalKey = intPreferencesKey("auto_refresh_interval")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    override fun getPrimaryColor(): Flow<Int> = context.dataStore.data.map { it[primaryColorKey] ?: 0xFFE50914.toInt() }
    override suspend fun setPrimaryColor(color: Int) {
        context.dataStore.edit { it[primaryColorKey] = color }
    }

    override fun getThemeMode(): Flow<String> = context.dataStore.data.map { it[themeModeKey] ?: "SYSTEM" }
    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[themeModeKey] = mode }
    }

    override fun getCornerRadius(): Flow<Int> = context.dataStore.data.map { it[cornerRadiusKey] ?: 12 }
    override suspend fun setCornerRadius(radius: Int) {
        context.dataStore.edit { it[cornerRadiusKey] = radius }
    }

    override fun getReadingMode(): Flow<String> = context.dataStore.data.map { it[readingModeKey] ?: "VERTICAL" }
    override suspend fun setReadingMode(mode: String) {
        context.dataStore.edit { it[readingModeKey] = mode }
    }

    override fun getLibraryLayout(): Flow<String> = context.dataStore.data.map { it[libraryLayoutKey] ?: "GRID" }
    override suspend fun setLibraryLayout(layout: String) {
        context.dataStore.edit { it[libraryLayoutKey] = layout }
    }

    override fun getIncognitoMode(): Flow<Boolean> = context.dataStore.data.map { it[incognitoKey] ?: false }
    override suspend fun setIncognitoMode(enabled: Boolean) {
        context.dataStore.edit { it[incognitoKey] = enabled }
    }

    override fun getAdultContent(): Flow<Boolean> = context.dataStore.data.map { it[adultContentKey] ?: false }
    override suspend fun setAdultContent(enabled: Boolean) {
        context.dataStore.edit { it[adultContentKey] = enabled }
    }

    override fun getBiometricEnabled(): Flow<Boolean> = context.dataStore.data.map { it[biometricEnabledKey] ?: false }
    override suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[biometricEnabledKey] = enabled }
    }

    override fun getAutoRefreshInterval(): Flow<Int> = context.dataStore.data.map { it[autoRefreshIntervalKey] ?: 0 }
    override suspend fun setAutoRefreshInterval(minutes: Int) {
        context.dataStore.edit { it[autoRefreshIntervalKey] = minutes }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = context.dataStore.data.map { it[onboardingCompletedKey] ?: false }
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[onboardingCompletedKey] = completed }
    }

    override suspend fun exportBackup(): String {
        return "{ \"version\": 1, \"settings\": {} }"
    }

    override suspend fun importBackup(json: String) {
        // Parse and apply settings
    }
}
