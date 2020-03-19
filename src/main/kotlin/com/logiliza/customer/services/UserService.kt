package com.logiliza.customer.services

import com.logiliza.customer.models.User
import com.logiliza.customer.models.UserRole
import io.vertx.core.Vertx
import io.vertx.mysqlclient.MySQLPool
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception

class UserService(
    private val vertx: Vertx,
    private val mysqlPool: MySQLPool
) {
    suspend fun createUser(email: String, password: String): User? {
        if(!userExists(email))
            return User("", email, "32323232", UserRole.NONE, 2)
        throw Exception("User already exists")
    }

    private fun userExists(email: String): Boolean {
        //todo
        //check if this email already exists
        return false
    }

    fun checkPassword(pwd: String, hashed: String): Boolean{
        return BCrypt.checkpw(pwd, hashed)
    }

    fun hashPassword(pwd: String): String {
        return BCrypt.hashpw(pwd, BCrypt.gensalt())
    }
}