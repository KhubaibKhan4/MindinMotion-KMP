package org.mind.app.presentation.ui.screens.resume

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.resume.ResumeData
import org.mind.app.domain.model.resume.ResumeItem
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

class ResumeScreen : Screen {
    @Composable
    override fun Content() {
        ResumeContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeContent() {
    val viewModel: MainViewModel = koinInject()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var resumeList by remember { mutableStateOf<List<ResumeItem>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf("All") }
    val isDark by LocalThemeIsDark.current
    val resumeState by viewModel.resumes.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isGirdView by remember { mutableStateOf(true) }
    val navigator = LocalNavigator.current

    LaunchedEffect(Unit) {
        viewModel.getAllResumes()
    }

    LaunchedEffect(resumeState) {
        if (resumeState is ResultState.Success) {
            resumeList = (resumeState as ResultState.Success).data
        }
    }

    val categories = listOf("All") + resumeList.map { it.categoryName }.distinct()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Templates")
                },
                actions = {
                    Icon(
                        imageVector = if (isGirdView) Icons.Default.GridView else Icons.AutoMirrored.Filled.ViewList,
                        contentDescription = null,
                        modifier = Modifier.clickable { isGirdView = !isGirdView }
                    )
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                TextField(
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
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        containerColor = Color.White,
                        focusedTrailingIconColor = if (isDark) Color.LightGray else Color.Gray,
                        unfocusedTrailingIconColor = if (isDark) Color.LightGray else Color.Gray,
                        focusedLeadingIconColor = if (isDark) Color.LightGray else Color.Gray,
                        unfocusedLeadingIconColor = if (isDark) Color.LightGray else Color.Gray
                    )
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        Text(
                            text = category,
                            color = if (selectedCategory == category) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier
                                .clickable {
                                    selectedCategory = category
                                }
                                .padding(8.dp)
                        )
                    }
                }

                val filteredResumes = resumeList.filter {
                    (selectedCategory == "All" || it.categoryName == selectedCategory) &&
                            (searchText.text.isEmpty() || it.categoryName.contains(
                                searchText.text,
                                ignoreCase = true
                            ))
                }

                if (resumeState is ResultState.Loading) {
                    LoadingBox()
                } else if (resumeState is ResultState.Error) {
                    val error = (resumeState as ResultState.Error).message
                    ErrorBox(error)
                } else if (filteredResumes.isEmpty()) {
                    Text("No resumes found", style = MaterialTheme.typography.bodyMedium)
                } else {
                    if (isGirdView) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
                        ) {
                            items(filteredResumes) { resume ->
                                KamelImage(
                                    resource = asyncPainterResource(BASE_URL + resume.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(300.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                        .clickable {
                                            navigator?.push(ResumeDetailScreen(imageUrl = resume.imageUrl))

                                        },
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(filteredResumes) { resume ->
                                KamelImage(
                                    resource = asyncPainterResource(BASE_URL + resume.imageUrl),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.primary,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                        .clickable {
                                            navigator?.push(ResumeDetailScreen(imageUrl = resume.imageUrl))
                                        },

                                    )
                            }
                        }
                    }
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
        TextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") })
        TextField(
            value = phone.value,
            onValueChange = { phone.value = it },
            label = { Text("Phone") })
        TextField(
            value = workExperience.value,
            onValueChange = { workExperience.value = it },
            label = { Text("Work Experience") })
        TextField(
            value = education.value,
            onValueChange = { education.value = it },
            label = { Text("Education") })
        TextField(
            value = skills.value,
            onValueChange = { skills.value = it },
            label = { Text("Skills") })
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