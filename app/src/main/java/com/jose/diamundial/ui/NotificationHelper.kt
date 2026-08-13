package com.jose.diamundial.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jose.diamundial.MainActivity
import com.jose.diamundial.R

class NotificationHelper(private val context: Context) {
    private val CHANNEL_ID = "daily_events_channel"

    fun getImportance(): Int {
        val prefs = context.getSharedPreferences("dia_mundial_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("notification_importance", NotificationManager.IMPORTANCE_DEFAULT)
    }

    fun createNotificationChannel() {
        val importance = getImportance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.deleteNotificationChannel(CHANNEL_ID)

            val channelName = context.getString(R.string.notification_channel_name)
            val channelDescription = context.getString(R.string.notification_channel_description)

            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                importance
            ).apply {
                description = channelDescription
                enableLights(importance >= NotificationManager.IMPORTANCE_DEFAULT)
                enableVibration(importance >= NotificationManager.IMPORTANCE_DEFAULT)
                if (importance <= NotificationManager.IMPORTANCE_LOW) {
                    setSound(null, null)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(title: String, message: String) {
        val importance = getImportance()

        if (importance <= NotificationManager.IMPORTANCE_MIN) {
            return
        }

        val notificationPriority = when (importance) {
            NotificationManager.IMPORTANCE_HIGH -> NotificationCompat.PRIORITY_HIGH
            NotificationManager.IMPORTANCE_DEFAULT -> NotificationCompat.PRIORITY_DEFAULT
            NotificationManager.IMPORTANCE_LOW -> NotificationCompat.PRIORITY_LOW
            else -> NotificationCompat.PRIORITY_MIN
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(notificationPriority)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}
