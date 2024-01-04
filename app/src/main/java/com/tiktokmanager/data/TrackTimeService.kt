package com.tiktokmanager.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tiktokmanager.R


class TrackTimeService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPolling()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        val notification = NotificationCompat.Builder(this, "manager")
            .setContentTitle("service started")
            .setContentText("tracking tiktok usage")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        startForeground(1, notification.build(), FOREGROUND_SERVICE_TYPE_HEALTH)
    }

    var test = true
    private fun startPolling() {
        val handler = Handler(Looper.getMainLooper())
        val tikTokCheckRunnable = object : Runnable {
            override fun run() {
                if (isTikTokRunning()) {
                    println("way")
                    if (test) {
                        test = false
                        showNotification()
                    }

                } else {
                    println("nay")
                }
                handler.postDelayed(
                    this,
                    5000
                )
            }
        }
        handler.post(tikTokCheckRunnable)
    }

    private fun showNotification() {
        val notification = NotificationCompat.Builder(this, "manager")
            .setContentTitle("hello")
            .setContentText("description?")
            .setSmallIcon(androidx.core.R.drawable.notification_icon_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //TODO: ask user for permission
            }
            notify(1, notification.build())
        }
    }

    private fun isTikTokRunning(): Boolean {
        val usageStatsManager =
            baseContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val startTime = System.currentTimeMillis() - 60 * 60 * 1000
        val endTime = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(startTime, endTime)
        val tikTokPackageName = "com.zhiliaoapp.musically"

        var isRunning = false
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)

            if (event.packageName == tikTokPackageName) {
                when (event.eventType) {
                    UsageEvents.Event.ACTIVITY_RESUMED -> {
                        isRunning = true
                    }

                    UsageEvents.Event.ACTIVITY_PAUSED,
                    UsageEvents.Event.ACTIVITY_STOPPED -> {
                        isRunning = false
                    }
                }
            }
        }
        return isRunning
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "manager"
            val descriptionText = "sample"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("manager", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}