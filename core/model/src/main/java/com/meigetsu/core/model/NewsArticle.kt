package com.meigetsu.core.model

data class NewsArticle(
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String?,
    val date: String,
    val source: String
)
