package com.vuzy.commons

import io.vertx.kotlin.sqlclient.closeAwait
import io.vertx.sqlclient.PreparedQuery
import io.vertx.sqlclient.SqlConnection

suspend fun <T> PreparedQuery.use(scope: suspend (state: PreparedQuery) -> T): T {
    try {
        return scope(this)
    } finally {
        this.closeAwait()
    }
}

suspend fun <T> SqlConnection.use(scope: suspend (state: SqlConnection) -> T): T {
    try {
        return scope(this)
    } finally {
        this.close()
    }
}