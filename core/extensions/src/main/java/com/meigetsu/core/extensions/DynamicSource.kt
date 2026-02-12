package com.meigetsu.core.extensions

import com.meigetsu.core.model.Chapter
import com.meigetsu.core.model.Episode
import kotlinx.serialization.Serializable

@Serializable
data class SourceDefinition(
    val metadata: ExtensionMetadata,
    val searchPath: String,
    val detailsPath: String,
    val streamPath: String,
    val selector: String? = null
)

class DynamicAnimeSource(private val definition: SourceDefinition) : AnimeSource {
    override val metadata: ExtensionMetadata = definition.metadata

    override suspend fun getStreamUrls(episode: Episode): List<StreamUrl> {
        // Implementation would fetch from metadata.baseUrl + definition.streamPath
        // and parse using selectors/regex defined in the JSON
        return emptyList()
    }

    override suspend fun search(query: String, page: Int): List<MediaSearchResult> {
        return emptyList()
    }
}
