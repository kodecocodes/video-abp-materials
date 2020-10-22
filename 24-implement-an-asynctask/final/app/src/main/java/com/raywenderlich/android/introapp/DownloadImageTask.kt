package com.raywenderlich.android.introapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL

/**
 * Loads an image in the BG, and sends it through a callback.
 */
class DownloadImageTask(private val onImageLoaded: (Bitmap) -> Unit) : AsyncTask<String, String, Bitmap>() {

  override fun doInBackground(vararg params: String?): Bitmap {
    val imagePath = params[0] ?: throw IllegalArgumentException("No url provided!")
    val imageUrl = URL(imagePath)
    val connection = imageUrl.openConnection() as HttpURLConnection

    connection.doInput = true
    connection.connect()

    val inputStream = connection.inputStream
    try {
      return BitmapFactory.decodeStream(inputStream)
    } catch (error: Throwable) {
      error.printStackTrace()
    }

    throw IllegalArgumentException("No image")
  }

  override fun onPostExecute(result: Bitmap?) {
    super.onPostExecute(result)

    if (result != null) {
      onImageLoaded(result)
    }
  }
}