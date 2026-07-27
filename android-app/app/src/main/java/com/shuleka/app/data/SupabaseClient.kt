package com.shuleka.app.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SupabaseClient {
    private const val TAG = "SupabaseClient"
    private const val SUPABASE_URL = "https://uiwgbviucbbqcxdkpdwa.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val httpClient by lazy {
        try {
            Log.d(TAG, "Creating HTTP client...")
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(this@SupabaseClient.json)
                }
                engine {
                    requestTimeout = 15000
                }
            }.also {
                Log.d(TAG, "HTTP client created successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create HTTP client", e)
            throw e
        }
    }

    suspend fun getPosts(category: String? = null): List<Post> {
        return try {
            Log.d(TAG, "Fetching posts for category: $category")
            val url = "$SUPABASE_URL/rest/v1/posts?select=*&order=created_at.desc"
            val response = httpClient.get(url) {
                header("apikey", SUPABASE_ANON_KEY)
                header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            }
            val text = response.bodyAsText()
            Log.d(TAG, "Response length: ${text.length}")
            val posts = json.decodeFromString<List<Post>>(text)
            Log.d(TAG, "Parsed ${posts.size} posts")
            if (category != null && category != "all") {
                posts.filter { it.category.equals(category, ignoreCase = true) }
            } else {
                posts
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching posts", e)
            emptyList()
        }
    }
}
