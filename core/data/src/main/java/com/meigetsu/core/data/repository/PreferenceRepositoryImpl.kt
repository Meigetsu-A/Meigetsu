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
    private val cornerRadiusKey = intPreferencesKey("corner_radius")
    private val readingModeKey = stringPreferencesKey("reading_mode")
    private val incognitoKey = booleanPreferencesKey("incognito_mode")
    private val adultContentKey = booleanPreferencesKey("adult_content")

    override fun getPrimaryColor(): Flow<Int> = context.dataStore.data.map { it[primaryColorKey] ?: 0xFF6650a4.toInt() }
    override suspend fun setPrimaryColor(color: Int) {
        context.dataStore.edit { it[primaryColorKey] = color }
    }

    override fun getCornerRadius(): Flow<Int> = context.dataStore.data.map { it[cornerRadiusKey] ?: 8 }
    override suspend fun setCornerRadius(radius: Int) {
        context.dataStore.edit { it[cornerRadiusKey] = radius }
    }

    override fun getReadingMode(): Flow<String> = context.dataStore.data.map { it[readingModeKey] ?: "VERTICAL" }
    override suspend fun setReadingMode(mode: String) {
        context.dataStore.edit { it[readingModeKey] = mode }
    }

    override fun getIncognitoMode(): Flow<Boolean> = context.dataStore.data.map { it[incognitoKey] ?: false }
    override suspend fun setIncognitoMode(enabled: Boolean) {
        context.dataStore.edit { it[incognitoKey] = enabled }
    }

    override fun getAdultContent(): Flow<Boolean> = context.dataStore.data.map { it[adultContentKey] ?: false }
    override suspend fun setAdultContent(enabled: Boolean) {
        context.dataStore.edit { it[adultContentKey] = enabled }
    }
}
