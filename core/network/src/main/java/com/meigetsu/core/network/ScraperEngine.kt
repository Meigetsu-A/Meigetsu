package com.meigetsu.core.network

import com.meigetsu.core.model.ScraperAction
import com.meigetsu.core.model.SelectorDefinition
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScraperEngine @Inject constructor(
    private val httpClient: HttpClient
) {
    suspend fun executeAction(
        baseUrl: String,
        action: ScraperAction,
        params: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): List<Map<String, String>> {
        var url = if (action.endpoint.startsWith("http")) action.endpoint else baseUrl + action.endpoint
        params.forEach { (key, value) ->
            url = url.replace("{$key}", value)
        }

        val response = httpClient.get(url) {
            headers.forEach { (key, value) ->
                header(key, value)
            }
        }.bodyAsText()

        val doc = Jsoup.parse(response)
        val elements = doc.select(action.selector)

        return elements.map { element ->
            action.fields.mapValues { (_, selector) ->
                evaluateSelector(element, selector)
            }
        }
    }

    private fun evaluateSelector(element: Element, definition: SelectorDefinition): String {
        val target = if (definition.selector.isEmpty()) element else element.selectFirst(definition.selector)
        var result = when {
            target == null -> ""
            definition.attribute != null -> target.attr(definition.attribute)
            else -> target.text()
        }

        definition.regex?.let { regexStr ->
            val regex = Regex(regexStr)
            result = regex.find(result)?.groupValues?.getOrNull(1) ?: result
        }

        definition.replace?.forEach { (old, new) ->
            result = result.replace(old, new)
        }

        return result
    }
}
