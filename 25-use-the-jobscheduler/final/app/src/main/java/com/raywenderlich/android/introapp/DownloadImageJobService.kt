package com.raywenderlich.android.introapp

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO - Add comment
 */
class DownloadImageJobService : JobService() {

  override fun onStartJob(params: JobParameters?): Boolean {
    val imagePath = params?.extras?.getString("image_path")

    return if (imagePath != null) {
      downloadImage(imagePath)
      true
    } else {
      jobFinished(null, false)
      false
    }
  }

  private fun downloadImage(imagePath: String) {
    Thread(Runnable {
      val imageUrl = URL(imagePath)
      val connection = imageUrl.openConnection() as HttpURLConnection
      connection.doInput = true
      connection.connect()

      val imageFilePath = "owl_image_${System.currentTimeMillis()}.jpg"
      val inputStream = connection.inputStream
      val file = File(applicationContext.externalMediaDirs.first(), imageFilePath)

      try {
        val outputStream = FileOutputStream(file)
        outputStream.use { output ->
          val buffer = ByteArray(4 * 1024)
          var byteCount = inputStream.read(buffer)

          while (byteCount > 0) {
            output.write(buffer, 0, byteCount)
            byteCount = inputStream.read(buffer)
          }

          output.flush()
        }

        sendBroadcast(Intent().apply {
          action = ACTION_IMAGE_DOWNLOADED
          putExtra("image_path", file.absolutePath)
        })
      } catch (error: Throwable) {
        error.printStackTrace()
        jobFinished(null, false)
      }
    }).start()
  }

  override fun onStopJob(params: JobParameters?): Boolean {
    return false
  }
}