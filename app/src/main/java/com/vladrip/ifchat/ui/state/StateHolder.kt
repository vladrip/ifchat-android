package com.vladrip.ifchat.ui.state

class StateHolder<T>(
    val state: T? = null,
    val status: Status = Status.SUCCESS,
) {
    enum class Status {
        SUCCESS,
        LOADING,
        NETWORK_ERROR,
        ERROR
    }
}