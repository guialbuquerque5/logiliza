package com.logiliza.customer.services

import com.logiliza.customer.models.Customer
import com.vuzy.commons.use
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.pgclient.PgPool

class CustomerService(
    private val vertx: Vertx,
    private val pgPool: PgPool,
    private val userService: UserService
) {
    suspend fun createCustomer(customer: Customer) {
        pgPool.connection.await().use { conn ->
            conn.prepare("""
                
            """.trimIndent()).await().use { pq ->
                val newCustomer = pq.execute().await().first()
                userService.createUser("", "")
            }
        }
    }
}