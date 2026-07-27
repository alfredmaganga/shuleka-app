package com.shuleka.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

object SupabaseClient {
    private const val TAG = "Shuleka"
    private const val BASE = "https://uiwgbviucbbqcxdkpdwa.supabase.co"
    private const val KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg"

    suspend fun getPosts(category: String? = null): List<Post> = withContext(Dispatchers.IO) {
        try {
            val url = URL("$BASE/rest/v1/posts?select=*&order=created_at.desc")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("apikey", KEY)
            conn.setRequestProperty("Authorization", "Bearer $KEY")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val response = conn.inputStream.bufferedReader().readText()
            conn.disconnect()

            val array = JSONArray(response)
            val posts = mutableListOf<Post>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                posts.add(
                    Post(
                        id = obj.optString("id", ""),
                        title = obj.optString("title", ""),
                        body = obj.optString("body", ""),
                        category = obj.optString("category", ""),
                        pdfUrl = obj.optString("pdf_url", ""),
                        createdAt = obj.optString("created_at", "")
                    )
                )
            }

            Log.d(TAG, "Loaded ${posts.size} posts")
            if (category != null && category != "all") {
                posts.filter { it.category.equals(category, ignoreCase = true) }
            } else {
                posts
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading posts", e)
            emptyList()
        }
    }
}
