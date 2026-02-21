package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.Media
import com.meigetsu.core.model.History
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibrary(): Flow<List<Media>>
    suspend fun addToLibrary(media: Media)
    suspend fun removeFromLibrary(id: String)
    fun getHistory(): Flow<List<History>>
    suspend fun updateHistory(mediaId: String, itemNumber: Double, position: Long)
    suspend fun getHistoryForMedia(mediaId: String): History?
}
