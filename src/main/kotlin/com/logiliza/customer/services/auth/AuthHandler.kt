package com.logiliza.customer.services.auth

import com.logiliza.customer.models.UserRole
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext


class AuthHandler(private val authService: AuthService): Handler<RoutingContext> {

    override fun handle(ctx: RoutingContext) {

        val token = ctx.request().getHeader(HttpHeaders.AUTHORIZATION)?.replace("Bearer ", "")
        if (token != null) {
            val user = authService.tokenToUser(token)
            if(user != null && authService.isAuthorized(
                    user,
                    listOf(UserRole.ADMIN, UserRole.NONE)
                )
            ) {
                ctx.put("user", user)
                ctx.next()
                return
            }
        }
        ctx.fail(401)


    }
}