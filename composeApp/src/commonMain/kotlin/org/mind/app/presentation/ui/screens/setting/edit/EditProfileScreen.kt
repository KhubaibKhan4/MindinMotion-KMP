package org.mind.app.presentation.ui.screens.setting.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.mind.app.domain.model.users.Users
import org.mind.app.utils.isValidAddress
import org.mind.app.utils.isValidCity
import org.mind.app.utils.isValidCountry
import org.mind.app.utils.isValidEmail
import org.mind.app.utils.isValidFullName
import org.mind.app.utils.isValidPhoneNumber
import org.mind.app.utils.isValidPostalCode

class EditProfileScreen(private val users: Users) : Screen {
    @Composable
    override fun Content() {
        EditProfileScreenContent(users)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreenContent(profile: Users) {
    val navigator = LocalNavigator.current
    var username by remember { mutableStateOf(profile.username) }
    var email by remember { mutableStateOf(profile.email) }
    var fullName by remember { mutableStateOf(profile.fullName) }
    var address by remember { mutableStateOf(profile.address) }
    var city by remember { mutableStateOf(profile.city) }
    var country by remember { mutableStateOf(profile.country) }
    var postalCode by remember { mutableStateOf(profile.postalCode.toString()) }
    var phoneNumber by remember { mutableStateOf(profile.phoneNumber) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var countryError by remember { mutableStateOf<String?>(null) }
    var postalCodeError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = padding.calculateTopPadding())
                .padding(start = 8.dp, end = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = if (isValidFullName(it)) null else "Invalid username"
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = usernameError != null,
                singleLine = true
            )
            if (usernameError != null) {
                Text(usernameError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (isValidEmail(it)) null else "Invalid email"
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                singleLine = true
            )
            if (emailError != null) {
                Text(emailError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    fullNameError = if (isValidFullName(it)) null else "Invalid full name"
                },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = fullNameError != null,
                singleLine = true
            )
            if (fullNameError != null) {
                Text(fullNameError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                    addressError = if (isValidAddress(it)) null else "Invalid address"
                },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = addressError != null,
                singleLine = true
            )
            if (addressError != null) {
                Text(addressError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = city,
                onValueChange = {
                    city = it
                    cityError = if (isValidCity(it)) null else "Invalid city"
                },
                label = { Text("City") },
                modifier = Modifier.fillMaxWidth(),
                isError = cityError != null,
                singleLine = true
            )
            if (cityError != null) {
                Text(cityError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = country,
                onValueChange = {
                    country = it
                    countryError = if (isValidCountry(it)) null else "Invalid country"
                },
                label = { Text("Country") },
                modifier = Modifier.fillMaxWidth(),
                isError = countryError != null,
                singleLine = true
            )
            if (countryError != null) {
                Text(countryError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = postalCode,
                onValueChange = {
                    postalCode = it
                    postalCodeError = if (isValidPostalCode(it)) null else "Invalid postal code"
                },
                label = { Text("Postal Code") },
                modifier = Modifier.fillMaxWidth(),
                isError = postalCodeError != null,
                singleLine = true
            )
            if (postalCodeError != null) {
                Text(postalCodeError!!, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it
                    phoneNumberError = if (isValidPhoneNumber(it)) null else "Invalid phone number"
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneNumberError != null,
                singleLine = true
            )
            if (phoneNumberError != null) {
                Text(phoneNumberError!!, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    val isValid =
                        usernameError == null && emailError == null && fullNameError == null &&
                                addressError == null && cityError == null && countryError == null &&
                                postalCodeError == null && phoneNumberError == null

                    if (isValid) {

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Save Profile")
            }
        }
    }
}