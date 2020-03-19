package com.vuzy.commons

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.lang.Exception

abstract class Serializable<T: Any> {
    val mapper = jacksonObjectMapper()

    public abstract var data: T

    fun serialize(): JsonObject {
        return JsonObject(mapper.writeValueAsString(this.data))
    }
    inline fun <reified E> deserialize(json: Any): E{
        try {
            return mapper.convertValue(json)
        } catch (e: Exception) {
            throw e
        }
    }
}

class ISODateSerializer() : JsonSerializer<DateTime>() {
    override fun serialize(value: DateTime?, gen: JsonGenerator?, provider: SerializerProvider?) {
        val map = hashMapOf("$"+"date" to ISODateTimeFormat.dateTime().print(value))
        gen?.writeObject(map)
    }
}
