package com.shuleka.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "shuleka_posts"
        private const val CHANNEL_NAME = "Taarifa Mpya"
        private const val CHANNEL_DESC = "Arifa za post mpya kutoka Shuleka"
        private const val PREFS_NAME = "shuleka_notifications"
        private const val KEY_LAST_POST_COUNT = "last_post_count"
        private const val KEY_LAST_POST_IDS = "last_post_ids"
        private const val CHECK_INTERVAL_MINUTES = 15L
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startPeriodicCheck() {
        executor.scheduleAtFixedRate({
            checkForNewPosts()
        }, CHECK_INTERVAL_MINUTES, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES)
    }

    private fun checkForNewPosts() {
        try {
            val url = java.net.URL("https://uiwgbviucbbqcxdkpdwa.supabase.co/rest/v1/posts?select=id,title,category&order=created_at.desc&limit=5")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.setRequestProperty("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg")
            conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVpd2didml1Y2JicWN4ZGtwZHdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUxNzI4MjIsImV4cCI6MjEwMDc0ODgyMn0.FN3z7fr4sbkMCIlTq_pXxccz0a-kqBUXghksoFYKJWg")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()

                val type = object : com.google.gson.reflect.TypeToken<List<Map<String, Any>>>() {}.type
                val posts: List<Map<String, Any>> = com.google.gson.Gson().fromJson(text, type)

                val currentIds = posts.mapNotNull { it["id"]?.toString() }.toSet()
                val savedIds = prefs.getStringSet(KEY_LAST_POST_IDS, emptySet()) ?: emptySet()

                if (savedIds.isNotEmpty()) {
                    val newIds = currentIds - savedIds
                    if (newIds.isNotEmpty()) {
                        val newPost = posts.firstOrNull { it["id"]?.toString() in newIds }
                        if (newPost != null) {
                            showNotification(
                                title = newPost["title"]?.toString() ?: "Post Mpya",
                                category = newPost["category"]?.toString() ?: ""
                            )
                        }
                    }
                }

                // Save current IDs
                prefs.edit().putStringSet(KEY_LAST_POST_IDS, currentIds).apply()
            }
        } catch (e: Exception) {
            // Silently fail - will retry on next interval
        }
    }

    private fun showNotification(title: String, category: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val categoryLabel = when (category.lowercase()) {
            "matokeo" -> "Matokeo"
            "taarifa" -> "Taarifa"
            "notes" -> "Notes"
            "vipimo" -> "Vipimo"
            else -> "Shuleka"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📚 $categoryLabel Mpya!")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun checkNow() {
        executor.execute { checkForNewPosts() }
    }
}
