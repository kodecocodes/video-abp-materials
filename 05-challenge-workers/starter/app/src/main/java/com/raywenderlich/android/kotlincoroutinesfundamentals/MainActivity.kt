/*
 * Copyright (c) 2020 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.kotlincoroutinesfundamentals

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    downloadImage()
  }

  private fun downloadImage() {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .setRequiredNetworkType(NetworkType.NOT_ROAMING)
        .build()

    val clearFilesWorker = OneTimeWorkRequestBuilder<FileClearWorker>()
        .build()

    val downloadRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
        .setConstraints(constraints)
        .build()

    val workManager = WorkManager.getInstance(this)
    workManager.beginWith(clearFilesWorker)
        .then(downloadRequest)
        .enqueue()

    workManager.getWorkInfoByIdLiveData(downloadRequest.id).observe(this, Observer { info ->
      if (info.state.isFinished) {
        val imagePath = info.outputData.getString("image_path")

        if (!imagePath.isNullOrEmpty()) {
          displayImage(imagePath)
        }
      }
    })
  }

  private fun displayImage(imagePath: String) {
    GlobalScope.launch(Dispatchers.Main) {
      val bitmap = loadImageFromFile(imagePath)

      image.setImageBitmap(bitmap)
    }
  }

  private suspend fun loadImageFromFile(imagePath: String) = withContext(Dispatchers.IO) {
    BitmapFactory.decodeFile(imagePath)
  }
}
