package com.logiliza.commons

import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.sqlclient.Row

inline fun <reified T> rowToDataClass(row: Row): T {
    val json = JsonObject()

    for (i in 0 until row.size()) {
        json.put(row.getColumnName(i), row.getValue(i)?.toString())
    }
    return jacksonObjectMapper().convertValue(json.map)
}