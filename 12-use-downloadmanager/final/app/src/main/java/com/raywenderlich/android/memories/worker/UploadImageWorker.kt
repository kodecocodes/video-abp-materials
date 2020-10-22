package com.raywenderlich.android.memories.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.raywenderlich.android.memories.App
import java.io.File

/**
 * TODO - Add comment
 */
class UploadImageWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {

  private val remoteApi by lazy { App.remoteApi }

  override suspend fun doWork(): Result {
    val imagePath = inputData.getString("image_path") ?: return Result.failure()
    val result = remoteApi.uploadImage(File(imagePath))

    return if (result.message == "Success") {
      Result.success()
    } else {
      Result.failure()
    }
  }
}