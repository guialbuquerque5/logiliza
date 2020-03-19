package com.logiliza.customer.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class CustomerType(type:String){
    INDIVIDUAL("INDIVIDUAL"),
    CORPORATION("CORPORATION")
}

enum class CustomerStatus(type:String){
    ACTIVE("ACTIVE"),
    FREE("FREE"),
    DEACTIVATE("DEACTIVATE")
}

data class Customer(
    val id: Int? = null,
    val name: String,
    @JsonProperty("document_type") val DocumentType: String,
    @JsonProperty("document_number") val DocumentNumber: String,
    val phone: String,
    val type: CustomerType,
    val status: CustomerStatus,
    val email: String,
    val website: String? = null
)