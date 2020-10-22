package com.raywenderlich.android.memories.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.raywenderlich.android.memories.networking.BASE_URL
import com.raywenderlich.android.memories.utils.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * TODO - Add comment
 */
class DownloadImageWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

  override fun doWork(): Result {
    val isAlreadyDownloaded = inputData.getBoolean("is_downloaded", false)
    val imageDownloadPath = inputData.getString("image_path") ?: return Result.failure()
    val parts = imageDownloadPath.split("/")

    if (isAlreadyDownloaded) {
      val imageFile = File(applicationContext.externalMediaDirs.first(), parts.last())
      return Result.success(workDataOf("image_path" to imageFile.absolutePath))
    }

    val imagePath = parts.last()
    val file = File(applicationContext.externalMediaDirs.first(), imagePath)

    FileUtils.downloadImage(file, imageDownloadPath)

    val output = workDataOf("image_path" to file.absolutePath)
    return Result.success(output)
  }
}