package com.davidmerchan.network.util

suspend inline fun <T> safeApiCall(crossinline call: suspend () -> T): Result<T> =
    try {
        Result.success(call())
    } catch (e: Exception) {
        Result.failure(e)
    }
