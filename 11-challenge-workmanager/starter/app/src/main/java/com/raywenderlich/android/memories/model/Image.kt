package com.raywenderlich.android.memories.model

import kotlinx.serialization.Serializable

/**
 * Holds the image path, for an uploaded image.
 */

@Serializable
data class Image(val imagePath: String)