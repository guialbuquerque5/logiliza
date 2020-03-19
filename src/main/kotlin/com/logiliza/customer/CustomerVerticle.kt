package com.logiliza.customer

import com.logiliza.customer.di.MainConfiguration
import com.logiliza.customer.resolvers.MainResolver
import com.logiliza.customer.resolvers.UserResolver
import com.logiliza.customer.services.auth.AuthHandler
import com.logiliza.customer.services.auth.AuthService
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.vertx.core.http.HttpMethod
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.graphql.GraphQLHandler
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions
import io.vertx.ext.web.handler.graphql.GraphiQLHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions
import io.vertx.kotlin.coroutines.await

class CustomerVerticle : CoroutineVerticle() {
    private lateinit var mainResolver: MainResolver
    private lateinit var authService: AuthService

    override suspend fun start() {
        LOG.info("Starting User Verticle")

        val config = vertx.orCreateContext.config()
        val mainConfig = MainConfiguration(vertx, config)

        mainResolver = mainConfig.mainResolver()
        authService = mainConfig.authService()

        val port = config.getJsonObject("server", JsonObject()).getInteger("port", 4425)

        val router = Router.router(vertx)
        router.route("/lib/*").handler(StaticHandler.create())
        router.route().handler(BodyHandler.create())
        configureCORS(config, router)
        configureGraphQL(router, authService)

        vertx.createHttpServer(
            HttpServerOptions().setCompressionSupported(true)
        ).requestHandler(router).listen(port)


        var server = vertx.createHttpServer()
        server.requestHandler { request ->

            // This handler gets called for each request that arrives on the server
            var response = request.response()
            response.putHeader("content-type", "text/plain")

            // Write to the response and end it


            //response.end(authService.authenticateUser("email", "senha"))
        }
        server.listen(8080)
    }

    private suspend fun configureGraphQL(router: Router, authService: AuthService) {
        LOG.info("Configuring GraphQL Handler")

        val graphql = GraphQL.newGraphQL(mainResolver.buildGraphqlSchema()).build()

        val options = GraphiQLHandlerOptions().setEnabled(true)

        router.route("/graphiql/static/*").handler(StaticHandler.create())
        router.route("/graphiql/*").handler(GraphiQLHandler.create(options))

        if (config.getBoolean("production")) {
            router.route("/graphql").handler(AuthHandler(authService))
        }

        router.route("/graphql").handler { ctx ->
            println(ctx.toString())
            println(ctx.bodyAsString)
            ctx.next()
        }

        router.route("/graphql").handler(
            GraphQLHandler.create(
                graphql, GraphQLHandlerOptions()
                    .setRequestBatchingEnabled(true)
            )
        )
    }


    companion object {
        private val LOG = LoggerFactory.getLogger(CustomerVerticle::class.java)

        private fun configureCORS(config: JsonObject, router: Router) {
            val cors = config.getJsonObject("server", JsonObject()).getString("cors", "http://localhost:4422")
            LOG.info("Setting CORS to $cors")

            router.route().handler(
                CorsHandler.create(cors)
                    .allowCredentials(true)
                    .allowedMethod(HttpMethod.OPTIONS)
                    .allowedMethod(HttpMethod.GET)
                    .allowedMethod(HttpMethod.POST)
                    .allowedMethod(HttpMethod.DELETE)
                    .allowedMethod(HttpMethod.PUT)
                    .allowedHeader("Authorization")
                    .allowedHeader("www-authenticate")
                    .allowedHeader("Content-Type")
                    .allowedHeader("Cookie")
            )
        }
    }

    suspend fun createGraphqlTypeDefinition(): TypeDefinitionRegistry {
        val schemaParser = SchemaParser()
        val typeDefinitionRegistry = TypeDefinitionRegistry()
        vertx.fileSystem().readDir("graphql").await().forEach { file ->
            typeDefinitionRegistry.merge(
                schemaParser.parse(vertx.fileSystem().readFile(file).await().toString())
            )
        }
        return typeDefinitionRegistry
    }
}


