package com.example.musicplayerkotlin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass:Application() {

    companion object{
        const val CHANNEL_ID = "songchannel"
        const val PLAY = "playsong"
        const val NEXT = "nextsong"
        const val PREVIOUS = "previoussong"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(CHANNEL_ID,"Rockstar Music Hd",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is important channel for playing songs..."
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}