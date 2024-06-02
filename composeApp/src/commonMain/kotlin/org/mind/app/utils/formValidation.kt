package org.mind.app.utils

import io.ktor.util.date.GMTDate
import kotlinx.datetime.Instant

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[A-Z]).{8,}$")
    return passwordRegex.matches(password)
}
fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val phoneRegex = Regex("^\\d{10}$")
    return phoneRegex.matches(phoneNumber)
}
fun isValidPostalCode(postalCode: String): Boolean {
    val postalRegex = Regex("^\\d{5}$")
    return postalRegex.matches(postalCode)
}
fun isValidAddress(address: String): Boolean {
    return address.isNotBlank()
}

fun isValidFullName(fullName: String): Boolean {
    return fullName.isNotBlank()
}

fun isValidCity(city: String): Boolean {
    return city.isNotBlank()
}

fun isValidCountry(country: String): Boolean {
    return country.isNotBlank()
}