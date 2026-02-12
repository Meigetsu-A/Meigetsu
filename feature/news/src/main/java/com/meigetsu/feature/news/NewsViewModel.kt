package com.meigetsu.feature.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meigetsu.core.common.Resource
import com.meigetsu.core.domain.repository.NewsRepository
import com.meigetsu.core.model.NewsArticle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _news = MutableStateFlow<Resource<List<NewsArticle>>>(Resource.Loading())
    val news: StateFlow<Resource<List<NewsArticle>>> = _news.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews() {
        newsRepository.getLatestNews().onEach {
            _news.value = it
        }.launchIn(viewModelScope)
    }
}
