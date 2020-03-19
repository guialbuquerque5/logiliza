package com.logiliza.customer.resolvers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.logiliza.customer.models.*
import com.logiliza.customer.services.UserService
import com.logiliza.customer.services.PositionService
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jwt.JWTOptions
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future
import java.util.*
import kotlin.collections.HashMap

class UserResolver constructor(
    private val vertx: Vertx,
    private val userService: UserService
): Resolver(vertx, "graphql/user.graphql") {
    private val mapper = ObjectMapper()

    fun wire(builder: RuntimeWiring.Builder) {
        builder.type("MainMutation") {
            it.dataFetcher("getUser", this::getUser)
        }
        builder.type("MainQuery") {
            it.dataFetcher("getUser", this::getUser)
        }
    }

    private fun getUser(env: DataFetchingEnvironment) = GlobalScope.future(vertx.dispatcher()) {
        val user = mapper.convertValue<User>(
            env.getArgument<HashMap<String, Any>>("user")
        )
        userService.createUser(user.email, "")
    }
}

