package com.meigetsu.core.network

import com.meigetsu.core.model.ScraperAction
import com.meigetsu.core.model.SelectorDefinition
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ScraperEngineTest {

    @Test
    fun `test executeAction parses HTML correctly`() = runBlocking {
        val mockEngine = MockEngine { request ->
            respond(
                content = """
                    <html>
                        <body>
                            <div class="item">
                                <h1 class="title">Title 1</h1>
                                <a href="/id1">Link 1</a>
                            </div>
                            <div class="item">
                                <h1 class="title">Title 2</h1>
                                <a href="/id2">Link 2</a>
                            </div>
                        </body>
                    </html>
                """.trimIndent(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/html")
            )
        }
        val httpClient = HttpClient(mockEngine)
        val scraperEngine = ScraperEngine(httpClient)

        val action = ScraperAction(
            endpoint = "/test",
            selector = ".item",
            fields = mapOf(
                "title" to SelectorDefinition(selector = ".title"),
                "id" to SelectorDefinition(selector = "a", attribute = "href", regex = "/(.*)")
            )
        )

        val results = scraperEngine.executeAction("https://example.com", action)

        assertEquals(2, results.size)
        assertEquals("Title 1", results[0]["title"])
        assertEquals("id1", results[0]["id"])
        assertEquals("Title 2", results[1]["title"])
        assertEquals("id2", results[1]["id"])
    }
}
