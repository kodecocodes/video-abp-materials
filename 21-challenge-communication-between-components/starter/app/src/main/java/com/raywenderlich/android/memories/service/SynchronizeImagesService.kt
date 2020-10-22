package com.raywenderlich.android.memories.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.raywenderlich.android.memories.App
import com.raywenderlich.android.memories.model.result.Success
import com.raywenderlich.android.memories.ui.main.MainActivity
import com.raywenderlich.android.memories.utils.FileUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * TODO - Add comment
 */

const val NOTIFICATION_CHANNEL_NAME = "Synchronize service channel"
const val NOTIFICATION_CHANNEL_ID = "Synchronize ID"

class SynchronizeImagesService : Service() {

  private val remoteApi by lazy { App.remoteApi }

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    showNotification()
    clearStorage()
    fetchImages()

    return START_NOT_STICKY
  }

  private fun showNotification() {
    createNotificationChannel()

    val notificationIntent = Intent(this, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    }

    val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle("Synchronization service")
        .setContentText("Downloading image")
        .setContentIntent(pendingIntent)
        .build()

    startForeground(1, notification)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_DEFAULT
      )

      val manager = getSystemService(NotificationManager::class.java)
      manager?.createNotificationChannel(serviceChannel)
    }
  }

  private fun clearStorage() {
    FileUtils.clearLocalStorage(applicationContext)
  }

  private fun fetchImages() {
    GlobalScope.launch {
      val result = remoteApi.getImages()

      if (result is Success) {
        val imagesArray = result.data.map { it.imagePath }.toTypedArray()

        FileUtils.queueImagesForDownload(applicationContext, imagesArray)
        stopForeground(true)
        sendBroadcast(Intent().apply {
          action = ACTION_IMAGES_SYNCHRONIZED
        })
      }
    }
  }
}