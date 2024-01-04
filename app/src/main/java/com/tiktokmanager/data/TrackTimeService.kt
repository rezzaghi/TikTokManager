package com.tiktokmanager.data

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
import androidx.annotation.RequiresApi


class TrackTimeService : Service() {

    //TODO: use hilt
    private val notificationManager = NotificationManager(context = this)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startPolling()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate() {
        super.onCreate()
        notificationManager.createNotificationChannel(
            channelId = "channelId",
            notificationName = "Track TikTok Time",
            notificationDescription = "Tracks how much time you spent on tiktok"
        )
        val notification = notificationManager.createNotification("Tracking TikTok usage", "")
        startForeground(1, notification, FOREGROUND_SERVICE_TYPE_HEALTH)
    }

    private fun startPolling() {
        val handler = Handler(Looper.getMainLooper())
        val tikTokCheckRunnable = object : Runnable {
            override fun run() {
                if (isTikTokRunning()) {
                    println("watching tiktok")
                    // Maybe the notification logic should go to some type of time manager class
                    notificationManager.showNotification(
                        notificationId = 1,
                        notificationManager.createNotification(
                            "time",
                            "get out of tiktok"
                        )
                    )
                } else {
                    println("outside of tiktok")
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
}