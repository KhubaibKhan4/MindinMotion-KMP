package org.mind.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json
import org.mind.app.domain.model.users.Users
import org.mind.app.utils.Constant.BASE_URL

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
    @OptIn(InternalAPI::class)
    suspend fun signUpUser(users: Users): Users {
        val formData = Parameters.build {
            append("username", "William")
            append("email", users.email)
            append("password", users.password)
            append("fullName", "William Smith")
            append("address", "123 Main Street")
            append("city", "New York")
            append("country","United States")
            append("postalCode", "134235")
            append("phoneNumber", "+1232445")
            append("userRole", users.userRole)
            append("imageUrl", "null")
        }
        return client.post(BASE_URL+"v1/users"){
            body = FormDataContent(formData)
        }.body()
    }
}