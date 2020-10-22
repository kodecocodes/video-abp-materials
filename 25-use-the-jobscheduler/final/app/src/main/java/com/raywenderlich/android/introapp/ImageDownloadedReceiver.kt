package com.raywenderlich.android.introapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * TODO - Add comment
 */
const val ACTION_IMAGE_DOWNLOADED = "image_downloaded"

class ImageDownloadedReceiver(
    private val onImageDownloaded: (String) -> Unit
) : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action != ACTION_IMAGE_DOWNLOADED) {
      return
    }
    val imagePath = intent.getStringExtra("image_path")

    if (imagePath != null) {
      onImageDownloaded(imagePath)
    }
  }
}