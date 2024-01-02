package com.tiktokmanager.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat


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
            .build()
        startForeground(1, notification, FOREGROUND_SERVICE_TYPE_HEALTH)
    }

    private fun startPolling() {
        val handler = Handler(Looper.getMainLooper())
        val tikTokCheckRunnable = object : Runnable {
            override fun run() {
                if (isTikTokRunning()) {
                    println("way")
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
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("manager", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}