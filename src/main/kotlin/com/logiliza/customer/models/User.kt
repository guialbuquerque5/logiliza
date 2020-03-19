package com.logiliza.customer.models
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer

enum class UserType {
    MASTER,
    ADMIN,
    USER,
    NONE
}

data class User(
    @JsonSerialize(
        using = ToStringSerializer::class
    )
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val type: UserType = UserType.NONE,
    val customerId: Int,
    val id: Int? = null
)