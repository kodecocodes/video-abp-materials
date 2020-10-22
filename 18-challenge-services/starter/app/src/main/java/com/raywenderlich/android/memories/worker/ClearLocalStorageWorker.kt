package com.raywenderlich.android.memories.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * TODO - Add comment
 */
class ClearLocalStorageWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

  override fun doWork(): Result {
    val rootFolder = applicationContext.externalMediaDirs.first()

    rootFolder?.listFiles()?.forEach {
      if (it.isDirectory) {
        it.deleteRecursively()
      } else {
        it.delete()
      }
    }
    return Result.success()
  }
}