package com.raywenderlich.android.memories.utils

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.raywenderlich.android.memories.networking.BASE_URL
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

object FileUtils {

  fun getImagePathFromInputStreamUri(
      uri: Uri,
      contentResolver: ContentResolver,
      context: Context
  ): String {
    var inputStream: InputStream? = null
    var filePath: String? = null
    val lastSegment = uri.lastPathSegment

    if (uri.authority != null) {
      try {
        inputStream = contentResolver.openInputStream(uri) // context needed
        val photoFile = createTemporalFileFrom(inputStream, context, lastSegment)

        filePath = photoFile!!.path

      } catch (e: FileNotFoundException) {
        // log
      } catch (e: IOException) {
        // log
      } finally {
        try {
          inputStream!!.close()
        } catch (e: IOException) {
          e.printStackTrace()
        }

      }
    }

    return filePath ?: "unknown.jpg"
  }

  private fun createTemporalFileFrom(
      inputStream: InputStream?,
      context: Context,
      lastSegment: String?
  ): File? {
    var targetFile: File? = null

    if (inputStream != null) {
      var read: Int
      val buffer = ByteArray(8 * 1024)

      targetFile = createTemporalFile(context, lastSegment)
      val outputStream = FileOutputStream(targetFile)

      read = inputStream.read(buffer)
      while (read != -1) {
        outputStream.write(buffer, 0, read)
        read = inputStream.read(buffer)
      }
      outputStream.flush()

      try {
        outputStream.close()
      } catch (e: IOException) {
        e.printStackTrace()
      }

    }

    return targetFile
  }

  private fun createTemporalFile(
      context: Context,
      lastSegment: String?): File {
    return File(context.externalCacheDir, "$lastSegment.jpg")
  }

  fun downloadImage(file: File, imageDownloadPath: String) {
    val imageUrl = URL("$BASE_URL/files/$imageDownloadPath")

    val connection = imageUrl.openConnection() as HttpURLConnection
    connection.doInput = true
    connection.connect()

    val inputStream = connection.inputStream

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
  }

  fun queueImagesForDownload(context: Context, images: Array<String>) {
    if (images.isNotEmpty()) {
      val downloadManager = context.getSystemService(DownloadManager::class.java)

      val requests = images.mapNotNull { imageUrl ->
        val file = File(context.externalMediaDirs.first(), imageUrl)

        buildDownloadManagerRequest(file, imageUrl)
      }

      requests.forEach { request -> downloadManager?.enqueue(request) }
    }
  }

  private fun buildDownloadManagerRequest(file: File, imageUrl: String): DownloadManager.Request {
    return DownloadManager.Request(Uri.parse("$BASE_URL/files/$imageUrl"))
        .setTitle("Image download")
        .setDescription("Downloading")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationUri(Uri.fromFile(file))
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(false)
  }

  fun clearLocalStorage(context: Context) {
    val rootFolder = context.externalMediaDirs.first()

    rootFolder?.listFiles()?.forEach {
      if (it.isDirectory) {
        it.deleteRecursively()
      } else {
        it.delete()
      }
    }
  }
}