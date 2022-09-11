package com.raywenderlich.android.kotlincoroutinesfundamentals

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO - Add comment
 */
class DownloadWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

  override fun doWork(): Result {
    val imageUrl = URL("https://cdn.pixabay.com/photo/2018/01/14/23/12/nature-3082832__480.jpg")
    val connection = imageUrl.openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()

    val imagePath = "owl_image.jpg"
    val inputStream = connection.inputStream
    val file = File(applicationContext.externalMediaDirs.first(), imagePath)

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

    return Result.success()
  }
}