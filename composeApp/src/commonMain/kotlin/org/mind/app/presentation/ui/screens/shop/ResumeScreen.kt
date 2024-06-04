package org.mind.app.presentation.ui.screens.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.cmppreference.LocalPreferenceProvider
import org.mind.app.domain.model.resume.ResumeData
import org.mind.app.theme.LocalThemeIsDark

class ResumeScreen : Screen {
    @Composable
    override fun Content() {
        ResumeContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeContent() {
    LocalPreferenceProvider {
        var searchText by remember { mutableStateOf(TextFieldValue("")) }
        val isDark by LocalThemeIsDark.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Templates")
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = null
                        )
                    }
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = it.calculateTopPadding()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    androidx.compose.material.TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = {
                            Text(
                                "Search...",
                                color = if (isDark) Color.LightGray else Color.Gray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(searchText.text.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        searchText = TextFieldValue("")
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(
                                Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
                        ,
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = androidx.compose.material.TextFieldDefaults.textFieldColors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            textColor = if (isDark) Color.White else Color.Black,
                            backgroundColor = Color.White,
                            trailingIconColor = if (isDark) Color.LightGray else Color.Gray,
                            leadingIconColor = if (isDark) Color.LightGray else Color.Gray
                        )
                    )
                }

            }
        }
    }
}
@Composable
fun ResumeForm(onSubmit: (ResumeData) -> Unit) {
    val name = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val workExperience = remember { mutableStateOf("") }
    val education = remember { mutableStateOf("") }
    val skills = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") })
        TextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Email") })
        TextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") })
        TextField(value = workExperience.value, onValueChange = { workExperience.value = it }, label = { Text("Work Experience") })
        TextField(value = education.value, onValueChange = { education.value = it }, label = { Text("Education") })
        TextField(value = skills.value, onValueChange = { skills.value = it }, label = { Text("Skills") })
        Button(onClick = {
            val resumeData = ResumeData(
                name = name.value,
                email = email.value,
                phone = phone.value,
                workExperience = workExperience.value,
                education = education.value,
                skills = skills.value
            )
            onSubmit(resumeData)
        }) {
            Text("Generate Resume")
        }
    }
}