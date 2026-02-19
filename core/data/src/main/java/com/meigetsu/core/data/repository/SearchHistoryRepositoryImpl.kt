package com.meigetsu.core.data.repository

import com.meigetsu.core.database.dao.SearchHistoryDao
import com.meigetsu.core.database.entity.SearchHistoryEntity
import com.meigetsu.core.domain.repository.SearchHistoryRepository
import com.meigetsu.core.model.SearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepositoryImpl @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) : SearchHistoryRepository {

    override fun getSearchHistory(): Flow<List<SearchHistory>> {
        return searchHistoryDao.getSearchHistory().map { entities ->
            entities.map { SearchHistory(it.query, it.query, it.timestamp) }
        }
    }

    override suspend fun addSearch(query: String) {
        if (query.isBlank()) return
        searchHistoryDao.insertSearch(SearchHistoryEntity(query, System.currentTimeMillis()))
    }

    override suspend fun removeSearch(query: String) {
        searchHistoryDao.deleteSearch(query)
    }

    override suspend fun clearHistory() {
        searchHistoryDao.clearHistory()
    }
}
