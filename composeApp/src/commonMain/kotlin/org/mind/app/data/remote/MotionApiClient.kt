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
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.mind.app.domain.model.category.QuizCategoryItem
import org.mind.app.domain.model.gemini.Gemini
import org.mind.app.domain.model.quiz.QuizQuestionsItem
import org.mind.app.domain.model.users.Users
import org.mind.app.utils.Constant.BASE_URL

object MotionApiClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                json = Json {
                    isLenient = true
                    ignoreUnknownKeys = true
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
    suspend fun getUserByEmail(email: String): Users{
        return client.get(BASE_URL+"v1/users/email/$email").body()
    }
    private val jsonDecoder = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    @OptIn(InternalAPI::class)
    suspend fun generateContent(content: String): Gemini {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=AIzaSyAGIbCm970chMEFc5fEiOLp0pxFvlrcN8E"

        val requestBody = mapOf(
            "contents" to listOf(
                mapOf("parts" to listOf(mapOf("text" to content)))
            )
        )

        try {
            val responseText: String = client.post(url) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                body = Json.encodeToString(requestBody)
            }.bodyAsText()

            println("API Response: $responseText")

            return jsonDecoder.decodeFromString(responseText)
        } catch (e: Exception) {
            println("Error during API request: ${e.message}")
            throw e
        }
    }
    suspend fun getAllCategories(): List<QuizCategoryItem>{
        return client.get(BASE_URL+"v1/category").body()
    }
    suspend fun getAllQuizzes(): List<QuizQuestionsItem>{
        return client.get(BASE_URL+"v1/quiz-questions").body()
    }
}