package com.vladrip.ifchat.ui.state

class StateHolder<T>(
    val state: T? = null,
    val status: Status = Status.SUCCESS,
    val uiMessage: String = ""
) {
    enum class Status {
        SUCCESS,
        LOADING,
        NETWORK_ERROR,
        ERROR
    }
}