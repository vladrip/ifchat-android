package com.vladrip.ifchat.data

class DataHolder<T>(
    val data: T? = null,
    val status: Status = Status.SUCCESS,
) {
    enum class Status {
        SUCCESS,
        LOADING,
        NETWORK_ERROR,
        ERROR
    }
}