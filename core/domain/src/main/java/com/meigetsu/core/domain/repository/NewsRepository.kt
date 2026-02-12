package com.meigetsu.core.domain.repository

import com.meigetsu.core.common.Resource
import com.meigetsu.core.model.NewsArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getLatestNews(): Flow<Resource<List<NewsArticle>>>
}
