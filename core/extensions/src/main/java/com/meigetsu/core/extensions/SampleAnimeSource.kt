package com.meigetsu.core.extensions

import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter

class SampleAnimeSource : AnimeSource {
    override val metadata: ExtensionMetadata = ExtensionMetadata(
        id = "sample-source",
        name = "Sample Source",
        version = "1.0.0",
        description = "A sample source for testing",
        iconUrl = null,
        type = "ANIME",
        baseUrl = "https://example.com"
    )

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        return listOf(
            StreamUrl("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "Auto", "m3u8")
        )
    }

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        return emptyList()
    }
}
