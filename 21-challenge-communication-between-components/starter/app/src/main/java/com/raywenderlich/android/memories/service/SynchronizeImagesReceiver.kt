package com.raywenderlich.android.memories.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * TODO - Add comment
 */
const val ACTION_IMAGES_SYNCHRONIZED = "images_synchronized"

class SynchronizeImagesReceiver(
    private inline val onImagesSynchronized: () -> Unit
) : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.action == ACTION_IMAGES_SYNCHRONIZED) {
      onImagesSynchronized()
    }
  }
}