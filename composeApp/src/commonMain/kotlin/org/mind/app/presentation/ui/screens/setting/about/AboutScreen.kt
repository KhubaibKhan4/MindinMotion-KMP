package org.mind.app.presentation.ui.screens.setting.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.ic_cyclone
import org.jetbrains.compose.resources.painterResource
import org.mind.app.theme.LocalThemeIsDark

class AboutScreen: Screen {
    @Composable
    override fun Content() {
        AboutScreenContent()
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreenContent() {
    val isDark by LocalThemeIsDark.current
    val navigator = LocalNavigator.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Mind & Motion") },
                navigationIcon = {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "About"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = padding.calculateTopPadding())
                    .padding(start = 8.dp, end = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_cyclone),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(color = if (isDark) Color.White else Color.Black)
                )
                Text(
                    text = "Mind & Motion",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your ultimate companion for academic success and personal development.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutSection(
                    title = "Our Mission",
                    content = "At Mind & Motion, we strive to empower learners of all ages to achieve their full potential through innovative educational resources and personalized support."
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutSection(
                    title = "Key Features",
                    content = "• Chat with Teachers\n• Take Quizzes\n• Create Resumes, Cover Letters & Motivational Letters\n• Access Educational Notes"
                )
                Spacer(modifier = Modifier.height(16.dp))
                AboutSection(
                    title = "Contact Us",
                    content = "For support or inquiries, please contact us at support@mindandmotion.com."
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "© 2024 Mind & Motion. All rights reserved.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    )
}

@Composable
fun AboutSection(title: String, content: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}