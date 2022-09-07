package com.bogleo.musicapp.common

data class Resource<out T>(
    val status: Status,
    val data: T?
) {
    companion object {
        fun<T> success(data: T?) = Resource(
            status = Status.Success(),
            data = data
        )

        fun<T> error(data: T?, message: String) = Resource(
            status = Status.Error(message = message),
            data = data
        )

        fun<T> loading(data: T?) = Resource(
            status = Status.Loading(),
            data = data
        )
    }
}

sealed class Status {
    class  Success : Status()
    class  Error<T>(val message: T) : Status()
    class  Loading : Status()
}