package org.mind.app.data.local

import app.cash.sqldelight.db.SqlDriver
import io.ktor.util.Platform
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mind.app.db.Message
import org.mind.app.db.MyDatabase

class DatabaseHelper(driver: SqlDriver) {
    private val database = MyDatabase(driver)
    private val queries = database.messageQueries

    fun getAllMessages(): Flow<List<Message>> {
        return flow {
            emit(queries.selectAllMessages().executeAsList())
        }
    }


    suspend fun insertMessage(text: String, isUserMessage: Boolean, timestamp: Long) {
        queries.insertMessage(text, if (isUserMessage) 1 else 0, timestamp)
    }
}