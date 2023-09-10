package com.vladrip.ifchat.data.network.model

data class PagedResponse<E>(
    val content: List<E>,
    val last: Boolean,
)