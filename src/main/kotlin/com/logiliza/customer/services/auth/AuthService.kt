package com.logiliza.customer.services.auth

import com.logiliza.customer.models.User
import com.logiliza.customer.models.UserType
import com.logiliza.customer.services.UserService
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.jwt.JWT
import io.vertx.ext.jwt.JWTOptions
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import org.mindrot.jbcrypt.BCrypt
import java.lang.Exception

class AuthService(
    private val jwt: JWTAuth,
    private val userService: UserService
) {
    fun tokenToUser(token: String) {
        jwt.authenticate(json {
            obj(
                "jwt" to token,
                "options" to obj("ignoreExpiration" to true)
            )
        }) { res ->
            if (res.succeeded()) {
                var session = res.result()
                println(session.toString())
            } else {
                // Failed!
            }
        }

    }

    /*
    fun tokenToUser(token: String, checkExpired: Boolean = true): User? {
        try {
            val decoded = jwt.authenticate()
            if (decoded != null && !jwt.isExpired(decoded, JWTOptions().setIgnoreExpiration(!checkExpired))) {
                return User(decoded.getString("email"))
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }
*/
    fun isAuthorized(user: User, types: List<UserType>): Boolean {
        return user.type in types
    }

    suspend fun login(email: String, password: String): String {
        val user = userService.getUserByEmail(email)
        if (user != null)
            if (checkPassword(password, user.password))
                return jwt.generateToken(json {
                    obj("sub" to user.id)
                    obj("customerId" to user.customerId)
                })
            else throw Exception("WRONG_PASSWORD")
        else throw Exception("USER_DONT_EXIST")
    }

    private fun checkPassword(pwd: String, hashed: String): Boolean {
        return true
        //return BCrypt.checkpw(pwd, hashed)
    }

    private fun hashPassword(pwd: String): String {
        return BCrypt.hashpw(pwd, BCrypt.gensalt())
    }

}