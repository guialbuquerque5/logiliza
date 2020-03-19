package com.logiliza.customer.services

import com.logiliza.commons.rowToDataClass
import com.logiliza.customer.models.User
import com.logiliza.customer.models.UserType
import com.vuzy.commons.use
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Tuple
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception

class UserService(
    private val vertx: Vertx,
    private val pgPool: PgPool
) {
    /*
    suspend fun createUser(email: String, password: String): User? {
        if(!userExists(email))
            return pgPool.connection.await().prepare("""
                INSERT INTO user ()
            """.trimIndent())
        throw Exception("User already exists")
    }*/

    suspend fun getUserByEmail(email: String): User? {
        return pgPool.connection.await().prepare("""
            SELECT * FROM user 
            WHERE email = $1
        """.trimIndent()).await().use { pq ->
            val user = pq.execute(Tuple.of(email)).await().firstOrNull()
            if (user != null) rowToDataClass<User>(user) else null
        }
    }

    suspend fun userExists(email: String): Boolean {
        return (getUserByEmail(email) != null)
    }
}