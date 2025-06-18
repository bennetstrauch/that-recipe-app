package com.plcoding.bookpedia.core.domain


sealed interface DataError: Error {
    enum class Remote: DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        SERVICE_UNAVAILABLE,
        UNKNOWN
    }

    enum class Local: DataError {
        DISK_FULL,
        NO_RECIPE_FOUND,
        UNKNOWN
    }
}