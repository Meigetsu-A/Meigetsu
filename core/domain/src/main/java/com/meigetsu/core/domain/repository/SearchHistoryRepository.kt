package com.meigetsu.core.domain.repository

import com.meigetsu.core.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun addSearch(query: String)
    suspend fun removeSearch(query: String)
    suspend fun clearHistory()
}
