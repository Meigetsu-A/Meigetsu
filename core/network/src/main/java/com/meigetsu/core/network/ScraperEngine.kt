package com.meigetsu.core.network
import android.content.Context
import app.cash.quickjs.QuickJs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ScraperEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient
) {
    fun execute(script: String, functionName: String, vararg args: Any): String {
        val quickJs = QuickJs.create()
        try {
            quickJs.evaluate(script)
            val result = quickJs.evaluate("$functionName(${args.joinToString(",") { "\"$it\"" }})")
            return result?.toString() ?: ""
        } finally {
            quickJs.close()
        }
    }
    suspend fun fetch(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { it.body?.string() ?: "" }
    }
}
