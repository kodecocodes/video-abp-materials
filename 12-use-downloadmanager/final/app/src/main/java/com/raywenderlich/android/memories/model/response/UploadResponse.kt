package com.raywenderlich.android.memories.model.response

import kotlinx.serialization.Serializable

/**
 * TODO - Add comment
 */

@Serializable
class UploadResponse(val message: String = "", val url: String = "")