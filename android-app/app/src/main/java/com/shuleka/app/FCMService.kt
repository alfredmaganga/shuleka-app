package com.shuleka.app

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        
        val title = message.notification?.title ?: message.data["title"] ?: "Shuleka"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val category = message.data["category"] ?: ""
        
        showNotification(title, body, category)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token refresh - could send to server if needed
    }

    private fun showNotification(title: String, body: String, category: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val categoryLabel = when (category.lowercase()) {
            "matokeo" -> "📋 Matokeo"
            "taarifa" -> "📢 Taarifa"
            "notes" -> "📝 Notes"
            "vipimo" -> "🧪 Vipimo"
            else -> "📚 Shuleka"
        }

        val notification = NotificationCompat.Builder(this, ShulekaApplication.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$categoryLabel Mpya!")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
