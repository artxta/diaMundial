package com.jose.diamundial.domain

data class Event(
    val date: String,
    val title: String,
    val description: String,
    val country: String = "España"
)