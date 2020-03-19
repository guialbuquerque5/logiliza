package com.logiliza.customer.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.vuzy.commons.ISODateSerializer
import com.vuzy.commons.Serializable
import org.joda.time.DateTime

class Position : Serializable<PositionData> {
    override var data: PositionData

    constructor(json: LinkedHashMap<String, Any>) {
        this.data = this.deserialize(json)
        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    }
}

data class PositionData(
    @JsonSerialize(
        using = ToStringSerializer::class
    )
    @JsonProperty("senderId") val senderId: Int,
    @JsonProperty("lat") val lat: Float? = null,
    @JsonProperty("long") val long: Float? = null,
    @JsonProperty("velocity") val velocity: Int? = null,
    @JsonProperty("battery") val battery: Int? = null,
    @JsonProperty("location")val location: Coordinate? = Coordinate("", listOf<Float?>(lat, 3.0F )),
    @JsonSerialize(
        using = ISODateSerializer::class
    )
    @JsonProperty("positionTime") val positionTime: DateTime? = DateTime(),
    val _id: String? = null
)
