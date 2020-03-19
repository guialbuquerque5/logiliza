package com.logiliza.customer.models

enum class PoiType {
    AREA,
    LOCATION,
}
data class Poi(
    val name: String,
    val type: PoiType
)