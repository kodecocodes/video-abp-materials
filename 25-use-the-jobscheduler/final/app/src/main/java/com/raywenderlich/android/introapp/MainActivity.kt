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

package com.raywenderlich.android.introapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Main Screen
 */
const val JOB_ID = 10

class MainActivity : AppCompatActivity() {

  private val receiver by lazy {
    ImageDownloadedReceiver { imagePath ->
      displayImage(imagePath)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    registerReceiver(receiver, IntentFilter().apply {
      addAction(ACTION_IMAGE_DOWNLOADED)
    })

    val jobScheduler = getSystemService(JobScheduler::class.java) ?: return

    jobScheduler.schedule(
        JobInfo.Builder(JOB_ID,
            ComponentName(this, DownloadImageJobService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setExtras(PersistableBundle().apply {
              putString("image_path", "https://cdn.pixabay.com/photo/2017/11/30/11/57/barn-owl-2988291_960_720.jpg")
            })
            .setOverrideDeadline(1500)
            .build()
    )
  }

  override fun onDestroy() {
    unregisterReceiver(receiver)
    super.onDestroy()
  }

  private fun displayImage(imagePath: String) {
    GlobalScope.launch(Dispatchers.Main) {
      val bitmap = loadImageFromFile(imagePath)

      image.setImageBitmap(bitmap)
    }
  }

  private suspend fun loadImageFromFile(imagePath: String): Bitmap =
      withContext(Dispatchers.IO) { BitmapFactory.decodeFile(imagePath) }
}