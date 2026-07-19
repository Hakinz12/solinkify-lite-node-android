package com.solink.solinkifymvpnode.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.solink.solinkifymvpnode.R


class LiteNodeForegroundService : Service() {

    private val nodeService = NodeService()

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        startForeground(
            1,
            createNotification()
        )

    }

    override fun onDestroy() {
        nodeService.stopNode()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            "lite_node_channel"
        )
            .setContentTitle("Solinkify Lite Node")
            .setContentText("Node is running...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "lite_node_channel",
                "Lite Node",
                NotificationManager.IMPORTANCE_LOW
            )

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)

        }

    }

}