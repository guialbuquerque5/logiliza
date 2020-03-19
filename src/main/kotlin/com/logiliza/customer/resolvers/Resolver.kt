package com.logiliza.customer.resolvers

import io.vertx.core.Vertx
import io.vertx.kotlin.core.file.readFileAwait
abstract class Resolver(private val vertx: Vertx, private val schema: String) {
    suspend fun getSchema(): String {
        return vertx.fileSystem().readFileAwait(schema).toString()
    }
}
