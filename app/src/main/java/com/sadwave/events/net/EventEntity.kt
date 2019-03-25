package com.sadwave.events.net

data class EventEntity(
    val date: Date? = null,
    val name: String? = null,
    val overview: String? = null,
    val photo: String? = null,
    val url: String? = null
)

data class Date(
    val date: String? = null,
    val time: String? = null
)