package com.meigetsu.core.data.repository

import com.meigetsu.core.model.*
import com.meigetsu.core.data.sources.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceRepository @Inject constructor(
    private val gogoAnimeSource: GogoAnimeSource,
    private val mangaFireSource: MangaFireSource
) {
    private val allSources = listOf(gogoAnimeSource, mangaFireSource)

    fun getSources(type: MediaType): List<Source> {
        return allSources.filter { it.type == type }
    }

    fun getSource(name: String): Source? {
        return allSources.find { it.name == name }
    }
}
