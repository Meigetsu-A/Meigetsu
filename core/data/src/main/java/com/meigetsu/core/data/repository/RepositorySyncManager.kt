package com.meigetsu.core.data.repository

import android.content.Context
import com.meigetsu.core.extensions.ExtensionManager
import com.meigetsu.core.extensions.UniversalProvider
import com.meigetsu.core.model.SourceDefinition
import com.meigetsu.core.network.ScraperEngine
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositorySyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val httpClient: HttpClient,
    private val extensionManager: ExtensionManager,
    private val scraperEngine: ScraperEngine
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun syncRepository(repoUrl: String) = withContext(Dispatchers.IO) {
        try {
            // Fetch list of source definitions from repo
            val definitions: List<SourceDefinition> = httpClient.get(repoUrl).body()

            val extensionsDir = context.getExternalFilesDir("extensions") ?: return@withContext
            if (!extensionsDir.exists()) extensionsDir.mkdirs()

            definitions.forEach { definition ->
                val file = File(extensionsDir, "${definition.id}.json")
                file.writeText(json.encodeToString(SourceDefinition.serializer(), definition))

                // Register it in the manager immediately
                val provider = UniversalProvider(definition, scraperEngine)
                extensionManager.registerExtension(provider)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
