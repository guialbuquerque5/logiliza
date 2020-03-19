package com.logiliza.customer.resolvers

import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await

class MainResolver(
    private val vertx: Vertx,
    private val userResolver: UserResolver,
    private val positionResolver: PositionResolver,
    private val customerResolver: CustomerResolver,
    private val sessionResolver: SessionResolver

): Resolver(vertx, "main.graphql"){

    suspend fun buildGraphqlSchema(): GraphQLSchema{
        val builder = RuntimeWiring.newRuntimeWiring()

        userResolver.wire(builder)
        positionResolver.wire(builder)
        customerResolver.wire(builder)
        sessionResolver.wire(builder)

        return graphql.schema.idl.SchemaGenerator()
            .makeExecutableSchema(createGraphqlTypeDefinition(), builder.build())
    }

    suspend fun createGraphqlTypeDefinition(): TypeDefinitionRegistry{
        val schemaParser = SchemaParser()
        return TypeDefinitionRegistry()
            .merge(
                schemaParser.parse(getSchema())
                    .merge(schemaParser.parse(userResolver.getSchema()))
                    .merge(schemaParser.parse(positionResolver.getSchema()))
            )

    }


}