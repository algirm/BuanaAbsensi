package com.papb.buanaabsensi.util

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure<T>(val throwable: Throwable?, val data: T?) : Resource<T>()
}
