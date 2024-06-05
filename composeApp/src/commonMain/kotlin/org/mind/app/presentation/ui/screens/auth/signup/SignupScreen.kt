package org.mind.app.presentation.ui.screens.auth.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.notify
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.utils.isValidAddress
import org.mind.app.utils.isValidCity
import org.mind.app.utils.isValidCountry
import org.mind.app.utils.isValidEmail
import org.mind.app.utils.isValidFullName
import org.mind.app.utils.isValidPassword
import org.mind.app.utils.isValidPhoneNumber
import org.mind.app.utils.isValidPostalCode
import kotlin.time.Duration.Companion.seconds

class SignupScreen : Screen {
    @Composable
    override fun Content() {
        SignupContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupContent(viewModel: MainViewModel = koinInject()) {
    val navigator = LocalNavigator.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("Student") }
    var passwordVisible by remember { mutableStateOf(false) }
    var cpasswordVisible by remember { mutableStateOf(false) }
    var userMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isUserCreated by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val state by viewModel.createUser.collectAsState()
    val serverState by viewModel.signupUsersServer.collectAsState()


    when (state) {
        is ResultState.Error -> {
            val error = (state as ResultState.Error).message
            userMessage = error
            notify(userMessage)
            isLoading = false
        }

        is ResultState.Loading -> {
        }

        is ResultState.Success -> {
            val response = (state as ResultState.Success).data
            userMessage = response
            notify(response)
            if (response.contains("Success") && !isUserCreated) {
                isUserCreated = true
                viewModel.signUpUserServer(
                    email,
                    password,
                    fullName.trim(),
                    fullName,
                    address,
                    city,
                    country,
                    postalCode,
                    phoneNumber,
                    userRole
                )
                scope.launch {
                    delay(2000)
                    isLoading = false
                    email = ""
                    password = ""
                    confirmPassword = ""
                    fullName = ""
                    address = ""
                    city = ""
                    country = ""
                    postalCode = ""
                    phoneNumber = ""
                    navigator?.pop()
                }
            }

        }
    }



    when (serverState) {
        is ResultState.Error -> {
            val error = (serverState as ResultState.Error).message
            userMessage = error
            ErrorBox(error)
            notify(error)
            isLoading = false
        }

        is ResultState.Loading -> {
            // handle loading state if needed
        }

        is ResultState.Success -> {
            val response = (serverState as ResultState.Success).data
            //userMessage = response
            //notify(response)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Sign Up",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = country,
                        onValueChange = { country = it },
                        label = { Text("Country") },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Postal Code") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        prefix = { Text("+") }
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, "")
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (cpasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (cpasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { cpasswordVisible = !cpasswordVisible }) {
                            Icon(imageVector = image, "")
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )
            }

            item {
                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(text = userRole)
                        Spacer(Modifier.weight(1f))
                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.90f).align(Alignment.Center)
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                userRole = "Student"
                                expanded = false
                            },
                            text = { Text("Student") },
                        )
                        DropdownMenuItem(onClick = {
                            userRole = "Teacher"
                            expanded = false
                        },
                            text = { Text("Teacher") }
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        when {
                            !isValidEmail(email) -> {
                                userMessage = "Invalid email format"
                            }

                            !isValidPassword(password) -> {
                                userMessage =
                                    "Password must be at least 8 characters long and contain an uppercase letter"
                            }

                            password != confirmPassword -> {
                                userMessage = "Passwords do not match"
                            }

                            !isValidPhoneNumber(phoneNumber) -> {
                                userMessage = "Invalid phone number format"
                            }

                            !isValidPostalCode(postalCode) -> {
                                userMessage = "Invalid postal code format"
                            }

                            !isValidFullName(fullName) -> {
                                userMessage = "Full name cannot be empty"
                            }

                            !isValidAddress(address) -> {
                                userMessage = "Address cannot be empty"
                            }

                            !isValidCity(city) -> {
                                userMessage = "City cannot be empty"
                            }

                            !isValidCountry(country) -> {
                                userMessage = "Country cannot be empty"
                            }

                            else -> {
                                viewModel.createAccount(
                                    email,
                                    password
                                )
                                isLoading = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Sign Up", fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        AnimatedVisibility(
                            visible = isLoading,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(25.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }
            }

            item {
                TextButton(
                    onClick = {
                        navigator?.push(LoginScreen)
                    }
                ) {
                    Text("Already have an account? Login")
                }
            }
            item {
                if (userMessage.isNotEmpty()) {
                    Text(text = userMessage, color = Color.Red)
                    scope.launch {
                        delay(2.seconds)
                        userMessage = ""
                    }
                }
            }
        }
    }
}