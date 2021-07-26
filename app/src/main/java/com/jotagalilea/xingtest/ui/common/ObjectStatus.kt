package com.jotagalilea.xingtest.ui.common

sealed class ObjectStatus<out T> {
    data class Success<T>(val data: T) : ObjectStatus<T>()
    data class Error<T>(val errorMessage: String?) : ObjectStatus<T>()
    data class Empty<T>(val emptyMessage: String?) : ObjectStatus<T>()
    class Loading<T> : ObjectStatus<T>()
}