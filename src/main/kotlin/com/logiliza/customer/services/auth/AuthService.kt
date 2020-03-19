package com.logiliza.customer.services.auth

import com.logiliza.customer.models.User
import com.logiliza.customer.models.UserRole
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.jwt.JWT
import io.vertx.ext.jwt.JWTOptions
import io.vertx.kotlin.core.json.get
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

class AuthService(
    private val jwt: JWTAuth
) {
    fun tokenToUser(){
        jwt.authenticate(json {
            obj(
                "jwt" to "BASE64-ENCODED-STRING",
                "options" to obj("ignoreExpiration" to true)
            )
        }) { res ->
            if (res.succeeded()) {
                var theUser = res.result()
                println(theUser.toString())
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
    fun isAuthorized(user: User, roles: List<UserRole>): Boolean {
        return user.role in roles
    }

    fun authenticateUser(email: String, password: String): String {
        return jwt.generateToken(json {
            obj("sub" to "paulo")
        })
    }
}