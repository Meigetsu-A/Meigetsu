package com.meigetsu.core.model

data class ExternalSource(
    val name: String,
    val url: String,
    val iconUrl: String?,
    val category: String,
    val status: String? = null
)
