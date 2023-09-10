package com.vladrip.ifchat.data.network.model

data class ErrorResponse(
    val status: Int,
    val error: String,
)