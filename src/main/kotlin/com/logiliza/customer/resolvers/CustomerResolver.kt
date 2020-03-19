package com.logiliza.customer.resolvers

import com.logiliza.customer.services.CustomerService
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class CustomerResolver(
    private val vertx: Vertx,
    private val customerService: CustomerService
): Resolver(vertx, "graphql/customer.graphql") {
    fun wire(builder: RuntimeWiring.Builder) {
        builder.type("MainMutation") {
            it.dataFetcher("registerClient", this::registerClient)
        }
    }

    private fun registerClient(env: DataFetchingEnvironment) = GlobalScope.future(vertx.dispatcher()) {
        customerService.createCustomer(env.getArgument("customer"))
    }
}

