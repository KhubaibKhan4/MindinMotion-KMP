package org.mind.app.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

fun formatTimestampToHumanReadable(timestamp: Long): String {
    val now = Clock.System.now()
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val messageDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val messageDate = messageDateTime.date
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val yesterday = today.minus(1, DateTimeUnit.DAY)

    return when {
        messageDate == today -> {
            messageDateTime.time.formatToAmPm()
        }

        messageDate == yesterday -> {
            "Yesterday"
        }

        else -> {
            messageDate.toString()
        }
    }
}

fun LocalTime.formatToAmPm(): String {
    val hour = if (this.hour % 12 == 0) 12 else this.hour % 12
    val minute = this.minute.toString().padStart(2, '0')
    val amPm = if (this.hour < 12) "AM" else "PM"
    return "$hour:$minute $amPm"
}

fun formatDateToGroup(timestamp: Long): String {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val messageDate = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date

    return when {
        messageDate == now -> "Today"
        messageDate == now.minus(1, DateTimeUnit.DAY) -> "Yesterday"
        else -> messageDate.toString()
    }
}

fun encodeEmail(email: String): String {
    return email.replace(".", ",")
}

fun decodeEmail(encodedEmail: String): String {
    return encodedEmail.replace(",", ".")
}
