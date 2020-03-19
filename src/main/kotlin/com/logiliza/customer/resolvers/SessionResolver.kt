package com.logiliza.customer.resolvers

import com.logiliza.customer.services.auth.AuthService
import graphql.GraphQL
import graphql.schema.DataFetcherFactoryEnvironment
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class SessionResolver(
    private val vertx: Vertx,
    private val authService: AuthService
): Resolver(vertx, "graphql/session.graphql") {

    fun wire(builder: RuntimeWiring.Builder) {
        builder.type("MainQuery") {
            it.dataFetcher("login", this::login)
        }
    }

    private fun login(env: DataFetchingEnvironment) = GlobalScope.future(vertx.dispatcher()) {
        authService.login(env.getArgument("email"), env.getArgument("password"))
    }

}