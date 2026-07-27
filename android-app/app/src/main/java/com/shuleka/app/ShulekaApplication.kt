package com.shuleka.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class ShulekaApplication : Application() {

    companion object {
        private const val TAG = "ShulekaApp"
        const val CHANNEL_ID = "shuleka_notifications"
    }

    override fun onCreate() {
        super.onCreate()
        
        createNotificationChannel()
        
        // Get FCM token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "FCM Token: $token")
        }
        
        // Subscribe to all users topic
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Subscribed to all_users topic")
                } else {
                    Log.w(TAG, "Failed to subscribe to topic", task.exception)
                }
            }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shuleka Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Arifa za Shuleka"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
