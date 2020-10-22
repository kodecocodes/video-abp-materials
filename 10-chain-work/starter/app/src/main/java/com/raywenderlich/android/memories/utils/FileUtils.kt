package com.raywenderlich.android.memories.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.*

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
}