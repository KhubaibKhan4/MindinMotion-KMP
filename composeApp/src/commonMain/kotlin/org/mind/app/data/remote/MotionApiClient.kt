package org.mind.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.mind.app.domain.model.users.Users

object MotionApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                json = Json {
                    isLenient = true
                }
            )
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 30000L
            requestTimeoutMillis = 30000L
            connectTimeoutMillis = 30000L
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            object : Logger {
                override fun log(message: String) {
                    println(message)
                }
            }
        }
    }
    suspend fun signUpUser(users: Users): Users {
        return client.get("v1/users").body()
    }
}