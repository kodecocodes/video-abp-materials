package com.raywenderlich.android.memories.model.result

/**
 * Result class to represent successful or failed requests.
 */
sealed class Result<out T : Any>

data class Success<out T : Any>(val data: T) : Result<T>()

data class Failure(val error: Throwable?) : Result<Nothing>()