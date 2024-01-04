package com.tiktokmanager.data

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationManager(private val context: Context) {
    @SuppressLint("MissingPermission")
    fun showNotification(notificationId: Int, notificationManagerCompat: Notification) {
        NotificationManagerCompat.from(context).notify(notificationId, notificationManagerCompat)
    }

    fun createNotification(title: String, description: String): Notification {
        return NotificationCompat.Builder(context, "manager")
            .setContentTitle(title)
            .setContentText(description)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    fun createNotificationChannel(
        channelId: String,
        notificationName: String,
        notificationDescription: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, notificationName, importance).apply {
                description = notificationDescription
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}