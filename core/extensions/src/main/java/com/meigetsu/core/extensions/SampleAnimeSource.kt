package com.meigetsu.core.extensions

import com.meigetsu.core.model.Episode
import com.meigetsu.core.model.Chapter

class SampleAnimeSource : AnimeSource {
    override val name: String = "Sample Source"
    override val version: String = "1.0.0"
    override val id: String = "sample-source"

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        return listOf(
            StreamUrl("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8", "Auto", "m3u8")
        )
    }
}
