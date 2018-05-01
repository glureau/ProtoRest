package com.glureau.geno.lib.repository

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T>(
        val status: Status,
        val data: T?,
        val message: String? = null,
        val throwable: Throwable? = null) {

    companion object {
        /**
         * Status of a resource that is provided to the UI.
         *
         *
         * These are usually created by the Repository classes where they return
         * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
         */
        enum class Status {
            SUCCESS,
            ERROR,
            LOADING
        }

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(msg: String?, data: T?, throwable: Throwable): Resource<T> {
            return Resource(Status.ERROR, data, msg, throwable)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data)
        }
    }
}