package com.logiliza.customer.services

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.DBObjectCodec
import com.logiliza.customer.models.Coordinate
import com.logiliza.customer.models.Position
import com.logiliza.customer.models.PositionData
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.ext.mongo.insertAwait
import org.bson.Document
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import java.util.*


class PositionService(
    private val mongoClient: MongoClient
) {
    val collection = "position"

    suspend fun insertPosition(position: Position): PositionData? {
        try {
            //val t = ISODateTimeFormat.dateTime().print(DateTime())
            //val q = JsonObject().put("publicationDate", JsonObject().put("$"+"date", t));

            val id = mongoClient.insertAwait(collection, position.serialize().apply {
                remove("_id")
                remove("lat")
                remove("long")
            })

            return position.data.copy(_id = id)
        } catch (e: Exception) {
            println(e.message)
            throw e
        }
    }
}
