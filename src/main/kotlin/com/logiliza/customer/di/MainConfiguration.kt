package com.logiliza.customer.di

import com.logiliza.customer.resolvers.CustomerResolver
import com.logiliza.customer.resolvers.MainResolver
import com.logiliza.customer.resolvers.PositionResolver
import com.logiliza.customer.resolvers.UserResolver
import com.logiliza.customer.services.CustomerService
import com.logiliza.customer.services.auth.AuthService
import com.logiliza.customer.services.UserService
import com.logiliza.customer.services.PositionService
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.mongo.MongoClient
import io.vertx.kotlin.ext.auth.jwt.jwtAuthOptionsOf
import io.vertx.kotlin.ext.auth.pubSecKeyOptionsOf
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.PoolOptions

class MainConfiguration(
    private val vertx: Vertx,
    private val config: JsonObject
) {
    private var mainResolver: MainResolver? = null
    private var positionResolver: PositionResolver? = null
    private var userResolver: UserResolver? = null
    private var customerResolver: CustomerResolver? = null

    private var mySQLPool: MySQLPool? = null
    private var pgPool: PgPool? = null
    private var mongoClient: MongoClient? = null
    
    private var userService: UserService? = null
    private var authService: AuthService? = null
    private var customerService: CustomerService? = null
    private var positionService: PositionService? = null

    private var jwt: JWTAuth? = null


    fun mySQLPool(): MySQLPool {
        if (mySQLPool == null) {
            val conf = config.getJsonObject("mysql")
            val connOptions = mySQLConnectOptionsOf(
                port = conf.getInteger("port"),
                host = conf.getString("host"),
                database = conf.getString("database"),
                user = conf.getString("user"),
                password = conf.getString("password")
            )
            val poolOptions = poolOptionsOf(maxSize = conf.getInteger("poolSize", 10))
            mySQLPool = MySQLPool.pool(vertx, connOptions, poolOptions)
        }

        return mySQLPool!!
    }

    fun pgSql(): PgPool {
        if (pgPool != null) {
            return pgPool!!
        }
        val conf = vertx.orCreateContext.config().getJsonObject("pgsql")

        val connOptions = pgConnectOptionsOf(
            port = conf.getInteger("port"),
            host = conf.getString("host"),
            database = conf.getString("database"),
            user = conf.getString("user"),
            password = conf.getString("password"),
            properties = mapOf("search_path" to conf.getString("schema"))
        )
        val poolOptions = PoolOptions().setMaxSize(conf.getInteger("poolSize"))
        pgPool = PgPool.pool(vertx, connOptions, poolOptions)
        return pgPool!!
    }

    fun mongoClient(): MongoClient {
        if (mongoClient != null)
            return mongoClient!!
        mongoClient = MongoClient.createShared(vertx, config.getJsonObject("mongoClient"))
        return mongoClient!!
    }

    fun mainResolver(): MainResolver {
        if(mainResolver == null)
            mainResolver = MainResolver(vertx,
                customerResolver = customerResolver(),
                userResolver = userResolver(),
                positionResolver = positionResolver())
        return mainResolver!!
    }

    fun userResolver(): UserResolver {
        if (userResolver == null) {
            userResolver = UserResolver(vertx, userService())
        }
        return userResolver!!
    }

    fun positionResolver(): PositionResolver {
        if(positionResolver == null) {
            positionResolver = PositionResolver(vertx, positionService())
        }
        return positionResolver!!
    }

    fun customerResolver(): CustomerResolver {
        if(customerResolver == null)
            customerResolver = CustomerResolver(vertx, customerService())
        return customerResolver!!
    }

    @Synchronized
    fun jwt(): JWTAuth {
        if (jwt == null) {
            jwt = JWTAuth.create(
                vertx,
                jwtAuthOptionsOf(
                    pubSecKeys = listOf(
                        pubSecKeyOptionsOf(
                            algorithm = "HS256",
                            publicKey = "MY_KEY_SECRET****",
                            symmetric = true
                        )
                    )
                )
            )
        }
        return jwt!!
    }

    fun userService(): UserService {
        if (userService == null) {
            userService = UserService(vertx, mySQLPool())
        }
        return userService!!
    }

    fun authService(): AuthService {
        if (authService == null) {
            authService = AuthService(jwt())
        }
        return authService!!
    }
    
    fun customerService(): CustomerService {
        if(customerService == null){
            customerService =
                CustomerService(vertx, pgSql(), userService())
        }
        return customerService!!
    }

    fun positionService(): PositionService {
        if(positionService == null){
            positionService = PositionService(mongoClient())
        }
        return positionService!!
    }
}
