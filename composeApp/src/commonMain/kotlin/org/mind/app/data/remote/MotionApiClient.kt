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
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
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
    suspend fun signUpUser(
        email: String,
        password: String,
        username: String,
        fullName: String,
        address: String,
        city: String,
        country: String,
        postalCode: String,
        phoneNumber: String,
        userRole: String,
    ): String {
        val formData = Parameters.build {
            append("username", username)
            append("email", email)
            append("password", password)
            append("fullName", fullName)
            append("address", address)
            append("city", city)
            append("country", country)
            append("postalCode", postalCode)
            append("phoneNumber", phoneNumber)
            append("userRole", userRole)
            append("imageUrl", "null")
        }
        return client.post(BASE_URL + "v1/signup") {
            body = FormDataContent(formData)
        }.body()
    }

    suspend fun getUsersById(userId: Int): Users {
        return client.get(BASE_URL + "v1/users/$userId").body()
    }

    @OptIn(InternalAPI::class)
    suspend fun updateUserDetails(
        userId: Int,
        email: String,
        username: String,
        fullName: String,
        address: String,
        city: String,
        country: String,
        postalCode: String,
        phoneNumber: String,
    ) {
        val formData = Parameters.build {
            append("username", username)
            append("email", email)
            append("fullName", fullName)
            append("address", address)
            append("city", city)
            append("country", country)
            append("postalCode", postalCode)
            append("phoneNumber", phoneNumber)
        }
        return client.put(BASE_URL + "v1/users/userDetail/$userId") {
            body = FormDataContent(formData)
        }.body()
    }

    @OptIn(InternalAPI::class)
    suspend fun loginServerUser(
        email: String,
        password: String,
    ): Users {
        val formData = Parameters.build {
            append("email", email)
            append("password", password)
        }
        return client.post(BASE_URL + "v1/login") {
            body = FormDataContent(formData)
        }.body()
    }
}