package com.logiliza.customer.resolvers

import com.logiliza.customer.models.Position
import com.logiliza.customer.services.PositionService
import graphql.schema.DataFetchingEnvironment
import graphql.schema.idl.RuntimeWiring
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.future.future

class PositionResolver(
    private var vertx: Vertx,
    private var positionService: PositionService
): Resolver(vertx, "graphql/position.graphql") {

    fun wire(builder: RuntimeWiring.Builder) {
        builder.type("MainMutation") {
            it.dataFetcher("insertPosition", this::insertPosition)
        }
    }

    private fun insertPosition(env: DataFetchingEnvironment) = GlobalScope.future(vertx.dispatcher()) {
        positionService.insertPosition(Position(env.getArgument("position")))
    }

}