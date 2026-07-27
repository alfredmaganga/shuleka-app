package com.shuleka.app.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

object SupabaseClient {
    private const val SUPABASE_URL = "https://uiwgbviucbbqcxdkpdwa.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        )
    }

    suspend fun getPosts(category: String? = null): List<Post> {
        return try {
            val posts = client.from("posts").select(Columns.ALL) {}.decodeList<Post>()
            val sorted = posts.sortedByDescending { it.createdAt }
            if (category != null && category != "all") {
                sorted.filter { it.category.equals(category, ignoreCase = true) }
            } else {
                sorted
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
