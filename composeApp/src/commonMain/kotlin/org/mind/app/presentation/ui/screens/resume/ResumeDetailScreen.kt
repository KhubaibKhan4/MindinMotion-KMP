package org.mind.app.presentation.ui.screens.resume

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.mind.app.domain.model.resume.Education
import org.mind.app.domain.model.resume.WorkExperience
import org.mind.app.generateResumePdf
import org.mind.app.saveResumeToFile
import org.mind.app.utils.Constant.BASE_URL

class ResumeDetailScreen(
    private val imageUrl: String,
) : Screen {
    @Composable
    override fun Content() {
        ResumeContent(imageUrl)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumeContent(imageUrl: String) {
    val navigator = LocalNavigator.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var workExperienceList by remember { mutableStateOf(mutableListOf<WorkExperience>()) }
    var educationList by remember { mutableStateOf(mutableListOf<Education>()) }
    var skillsList by remember { mutableStateOf(mutableListOf<String>()) }
    var newSkill by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Resume Details")
                },
                navigationIcon = {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = it.calculateTopPadding())
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            KamelImage(
                resource = asyncPainterResource(BASE_URL + imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = summary,
                onValueChange = { summary = it },
                label = { Text("Summary") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Work Experience", style = MaterialTheme.typography.headlineSmall)
            workExperienceList.forEachIndexed { index, experience ->
                WorkExperienceEntry(
                    experience = experience,
                    onRemove = {
                        workExperienceList =
                            workExperienceList.toMutableList().apply { removeAt(index) }
                    }
                )
            }
            Button(onClick = {
                workExperienceList =
                    workExperienceList.toMutableList().apply { add(WorkExperience()) }
            }) {
                Text("Add Work Experience")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Education", style = MaterialTheme.typography.headlineSmall)
            educationList.forEachIndexed { index, education ->
                EducationEntry(
                    education = education,
                    onRemove = {
                        educationList = educationList.toMutableList().apply { removeAt(index) }
                    }
                )
            }
            Button(onClick = {
                educationList = educationList.toMutableList().apply { add(Education()) }
            }) {
                Text("Add Education")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Skills", style = MaterialTheme.typography.headlineSmall)
            skillsList.forEachIndexed { index, skill ->
                SkillEntry(
                    skill = skill,
                    onRemove = {
                        skillsList = skillsList.toMutableList().apply { removeAt(index) }
                    })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSkill,
                    onValueChange = { newSkill = it },
                    label = { Text("New Skill") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (newSkill.isNotBlank()) {
                            skillsList = skillsList.toMutableList().apply { add(newSkill) }
                            newSkill = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Add Skill")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val resumeHtml = createResumeHtml(
                        name = "John Doe",
                        email = "johndoe@example.com",
                        phoneNumber = "123-456-7890",
                        location = "123 Main St, Anytown, USA",
                        summary = "A passionate software developer with experience in building Android applications.",
                        workExperienceList = workExperienceList,
                        educationList = educationList,
                        skillsList = skillsList
                    )
                    scope.launch {
                        val pdfData = generateResumePdf(resumeHtml)
                        saveResumeToFile(pdfData, "resume.pdf")
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Create Resume")
            }

        }
    }
}

@Composable
fun WorkExperienceEntry(experience: WorkExperience, onRemove: () -> Unit) {
    var companyName by remember { mutableStateOf(experience.companyName) }
    var jobTitle by remember { mutableStateOf(experience.jobTitle) }
    var startDate by remember { mutableStateOf(experience.startDate) }
    var endDate by remember { mutableStateOf(experience.endDate) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = companyName,
            onValueChange = {
                companyName = it
                experience.companyName = it
            },
            label = { Text("Company Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = jobTitle,
            onValueChange = {
                jobTitle = it
                experience.jobTitle = it
            },
            label = { Text("Job Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = startDate,
            onValueChange = {
                startDate = it
                experience.startDate = it
            },
            label = { Text("Start Date") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = {
                endDate = it
                experience.endDate = it
            },
            label = { Text("End Date") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onRemove, modifier = Modifier.align(Alignment.End)) {
            Text("Remove")
        }
    }
}

@Composable
fun EducationEntry(education: Education, onRemove: () -> Unit) {
    var degreeName by remember { mutableStateOf(education.degreeName) }
    var universityName by remember { mutableStateOf(education.universityName) }
    var sessionYear by remember { mutableStateOf(education.sessionYear) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = degreeName,
            onValueChange = {
                degreeName = it
                education.degreeName = it
            },
            label = { Text("Degree Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = universityName,
            onValueChange = {
                universityName = it
                education.universityName = it
            },
            label = { Text("University Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = sessionYear,
            onValueChange = {
                sessionYear = it
                education.sessionYear = it
            },
            label = { Text("Session Year") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = onRemove, modifier = Modifier.align(Alignment.End)) {
            Text("Remove")
        }
    }
}

@Composable
fun SkillEntry(skill: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(skill)
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
    }
}

fun createResumeHtml(
    name: String,
    email: String,
    phoneNumber: String,
    location: String,
    summary: String,
    workExperienceList: List<WorkExperience>,
    educationList: List<Education>,
    skillsList: List<String>,
): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <title>$name - Resume</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    margin: 0;
                    padding: 0;
                }
                .container {
                    width: 80%;
                    margin: 20px auto;
                    padding: 20px;
                    border: 1px solid #ddd;
                    border-radius: 10px;
                    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                }
                .header, .section {
                    margin-bottom: 20px;
                }
                .header img {
                    display: block;
                    margin: 0 auto;
                    width: 150px;
                    height: 150px;
                    border-radius: 50%;
                    border: 2px solid #007BFF;
                }
                .header h1 {
                    text-align: center;
                    font-size: 24px;
                    margin: 10px 0;
                }
                .header p {
                    text-align: center;
                    margin: 5px 0;
                }
                .section h2 {
                    font-size: 20px;
                    border-bottom: 2px solid #007BFF;
                    padding-bottom: 5px;
                    margin-bottom: 10px;
                }
                .section table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .section th, .section td {
                    text-align: left;
                    padding: 8px;
                    border-bottom: 1px solid #ddd;
                }
                .section tr:nth-child(even) {
                    background-color: #f2f2f2;
                }
                .skills ul {
                    list-style-type: none;
                    padding: 0;
                }
                .skills li {
                    background: #007BFF;
                    color: #fff;
                    padding: 8px;
                    margin: 5px 0;
                    border-radius: 5px;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>$name</h1>
                    <p>$email</p>
                    <p>$phoneNumber</p>
                    <p>$location</p>
                </div>
                <div class="section">
                    <h2>Summary</h2>
                    <p>$summary</p>
                </div>
                <div class="section">
                    <h2>Work Experience</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Company Name</th>
                                <th>Job Title</th>
                                <th>Start Date</th>
                                <th>End Date</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${
        workExperienceList.joinToString("\n") { experience ->
            "<tr>" +
                    "<td>${experience.companyName}</td>" +
                    "<td>${experience.jobTitle}</td>" +
                    "<td>${experience.startDate}</td>" +
                    "<td>${experience.endDate}</td>" +
                    "</tr>"
        }
    }
                        </tbody>
                    </table>
                </div>
                <div class="section">
                    <h2>Education</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>Degree Name</th>
                                <th>University Name</th>
                                <th>Session Year</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${
        educationList.joinToString("\n") { education ->
            "<tr>" +
                    "<td>${education.degreeName}</td>" +
                    "<td>${education.universityName}</td>" +
                    "<td>${education.sessionYear}</td>" +
                    "</tr>"
        }
    }
                        </tbody>
                    </table>
                </div>
                <div class="section skills">
                    <h2>Skills</h2>
                    <ul>
                        ${
        skillsList.joinToString("\n") { skill ->
            "<li>$skill</li>"
        }
    }
                    </ul>
                </div>
            </div>
        </body>
        </html>
    """.trimIndent()
}