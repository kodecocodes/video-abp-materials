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

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.min


object ImageUtils {

  //grayscale multipliers
  private const val GRAYSCALE_RED = 0.3
  private const val GRAYSCALE_GREEN = 0.59
  private const val GRAYSCALE_BLUE = 0.11

  private const val MAX_COLOR = 255

  private const val SEPIA_TONE_RED = 110
  private const val SEPIA_TONE_GREEN = 65
  private const val SEPIA_TONE_BLUE = 20

  fun applySepiaFilter(bitmap: Bitmap): Bitmap {
    // image size
    val width = bitmap.width
    val height = bitmap.height

    // create output bitmap
    val outputBitmap = Bitmap.createBitmap(width, height, bitmap.config)

    // color information
    var alpha: Int
    var red: Int
    var green: Int
    var blue: Int
    var currentPixel: Int

    // scan through all pixels
    for (x in 0 until width) {
      for (y in 0 until height) {

        // get pixel color
        currentPixel = bitmap.getPixel(x, y)

        // get color on each channel
        alpha = Color.alpha(currentPixel)
        red = Color.red(currentPixel)
        green = Color.green(currentPixel)
        blue = Color.blue(currentPixel)

        // apply grayscale sample
        red = (GRAYSCALE_RED * red + GRAYSCALE_GREEN * green + GRAYSCALE_BLUE * blue).toInt()
        green = red
        blue = green

        // apply intensity level for sepid-toning on each channel
        red += SEPIA_TONE_RED
        green += SEPIA_TONE_GREEN
        blue += SEPIA_TONE_BLUE

        //if you overflow any color, set it to MAX (255)
        red = min(red, MAX_COLOR)
        green = min(green, MAX_COLOR)
        blue = min(blue, MAX_COLOR)

        outputBitmap.setPixel(x, y, Color.argb(alpha, red, green, blue))
      }
    }

    bitmap.recycle()

    return outputBitmap
  }
}