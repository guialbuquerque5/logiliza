package com.logiliza.customer.models

data class Session(
    val name: String,
    val userId: String,
    val clientId: String,
    val role: String
)