package com.meigetsu.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.meigetsu.core.domain.repository.PreferenceRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferenceRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PreferenceRepository {

    private object PreferencesKeys {
        val PRIMARY_COLOR = longPreferencesKey("primary_color")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val CORNER_RADIUS = intPreferencesKey("corner_radius")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    override fun getPrimaryColor(): Flow<Long> = context.dataStore.data.map {
        it[PreferencesKeys.PRIMARY_COLOR] ?: 0xFF00BFFF
    }

    override suspend fun setPrimaryColor(color: Long) {
        context.dataStore.edit { it[PreferencesKeys.PRIMARY_COLOR] = color }
    }

    override fun getThemeMode(): Flow<String> = context.dataStore.data.map {
        it[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    override suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME_MODE] = mode }
    }

    override fun getCornerRadius(): Flow<Int> = context.dataStore.data.map {
        it[PreferencesKeys.CORNER_RADIUS] ?: 16
    }

    override suspend fun setCornerRadius(radius: Int) {
        context.dataStore.edit { it[PreferencesKeys.CORNER_RADIUS] = radius }
    }

    override fun getBiometricEnabled(): Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.BIOMETRIC_ENABLED] ?: false
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.BIOMETRIC_ENABLED] = enabled }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> = context.dataStore.data.map {
        it[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.ONBOARDING_COMPLETED] = completed }
    }
}
