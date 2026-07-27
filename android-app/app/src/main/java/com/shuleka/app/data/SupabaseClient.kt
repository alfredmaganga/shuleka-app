package com.shuleka.app.data

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SupabaseClient {
    private const val SUPABASE_URL = "https://uiwgbviucbbqcxdkpdwa.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg"

    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(this@SupabaseClient.json)
        }
    }

    suspend fun getPosts(category: String? = null): List<Post> {
        return try {
            val url = "$SUPABASE_URL/rest/v1/posts?select=*&order=created_at.desc"
            val response = httpClient.get(url) {
                header("apikey", SUPABASE_ANON_KEY)
                header("Authorization", "Bearer $SUPABASE_ANON_KEY")
            }
            val text = response.bodyAsText()
            val posts = json.decodeFromString<List<Post>>(text)
            if (category != null && category != "all") {
                posts.filter { it.category.equals(category, ignoreCase = true) }
            } else {
                posts
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
