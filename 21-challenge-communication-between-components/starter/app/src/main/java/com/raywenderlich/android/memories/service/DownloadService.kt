package com.raywenderlich.android.memories.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.raywenderlich.android.memories.utils.FileUtils
import com.raywenderlich.android.memories.utils.toast
import java.io.File

/**
 * TODO - Add comment
 */

const val SERVICE_NAME = "Download image service"

class DownloadService : JobIntentService() {

  companion object {
    private const val JOB_ID = 10

    fun startWork(context: Context, intent: Intent) {
      enqueueWork(context, DownloadService::class.java, JOB_ID, intent)
    }
  }

  override fun onHandleWork(intent: Intent) {
    val imagePath = intent.getStringExtra("image_path")

    if (imagePath != null) {
      downloadImage(imagePath)
    } else {
      Log.d("Missing image path", "Stopping service")
      stopSelf()
    }
  }

  private fun downloadImage(imagePath: String) {
    val file = File(applicationContext.externalMediaDirs.first(), imagePath)

    FileUtils.downloadImage(file, imagePath)
  }

  override fun onDestroy() {
    applicationContext?.toast("Stopping service!")
    super.onDestroy()
  }
}