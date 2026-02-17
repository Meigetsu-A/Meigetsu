package com.meigetsu.core.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceDefinition(
    val id: String,
    val name: String,
    val type: String, // "ANIME" or "MANGA"
    val baseUrl: String,
    val version: String,
    val search: ScraperAction,
    val popular: ScraperAction,
    val latest: ScraperAction,
    val episodes: ScraperAction? = null,
    val chapters: ScraperAction? = null,
    val streamUrls: ScraperAction? = null,
    val pages: ScraperAction? = null,
    val headers: Map<String, String> = emptyMap()
)

@Serializable
data class ScraperAction(
    val endpoint: String,
    val selector: String,
    val fields: Map<String, SelectorDefinition>
)

@Serializable
data class SelectorDefinition(
    val selector: String,
    val attribute: String? = null, // null for text
    val regex: String? = null,
    val replace: Map<String, String>? = null
)
