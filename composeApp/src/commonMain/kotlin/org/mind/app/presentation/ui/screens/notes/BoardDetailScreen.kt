package org.mind.app.presentation.ui.screens.notes

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.biology
import mind_in_motion.composeapp.generated.resources.chemistry
import mind_in_motion.composeapp.generated.resources.english
import mind_in_motion.composeapp.generated.resources.ic_cyclone
import mind_in_motion.composeapp.generated.resources.islamic
import mind_in_motion.composeapp.generated.resources.math
import mind_in_motion.composeapp.generated.resources.omputer
import mind_in_motion.composeapp.generated.resources.physics
import mind_in_motion.composeapp.generated.resources.social_studies
import mind_in_motion.composeapp.generated.resources.urdu_icon
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.papers.Classe
import org.mind.app.domain.model.papers.Papers
import org.mind.app.domain.model.papers.Subject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.notify
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class BoardDetailScreen(
    private val boards: Boards,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: MainViewModel = koinInject()
        LaunchedEffect(Unit) {
            viewModel.getAllPapers(boards.id.toLong())
        }
        val state by viewModel.papers.collectAsState()
        when (state) {
            is ResultState.Error -> {
                val error = (state as ResultState.Error).message
                ErrorBox(error)
            }

            ResultState.Loading -> {
                LoadingBox()
            }

            is ResultState.Success -> {
                val papers = (state as ResultState.Success).data
                BoardDetailsScreen(papers)
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDetailsScreen(board: Papers) {
    val navigator = LocalNavigator.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = board.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator?.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(top = it.calculateTopPadding())
                    .padding(horizontal = 6.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                board.classes.forEach { classItem ->
                    if (classItem.subjects.isNotEmpty()) {
                        ClassItem(classItem)
                    }
                }
            }
        }
    )
}

@Composable
fun ClassItem(classItem: Classe) {
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = classItem.title,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = Color(0xFF6200EE)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(classItem.subjects) { subject ->
                SubjectItem(subject)
            }
        }
    }
}

@Composable
fun SubjectItem(subject: Subject) {
    val navigator = LocalNavigator.current
    val isDark by LocalThemeIsDark.current
    val subjectImage = getSubjectImage(subject.title)
    var isError by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .clickable {
                if (subject.papers?.isNotEmpty() == true) {
                    navigator?.push(PaperViewScreen(subject.papers.first()))
                } else {
                    isError = true
                }
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isError) {
            notify("No Papers Available")
        }
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = subjectImage,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Crop
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = subject.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isDark) Color.White else Color.Black
                )
                if (subject.papers?.isNotEmpty() == true) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "PDF Available",
                        tint = Color(0xFF6200EE)
                    )
                }
            }
        }
    }
}

@Composable
private fun getSubjectImage(subjectName: String): Painter {
    val subjectImageMap = mapOf(
        "English" to Res.drawable.english,
        "Mathematics" to Res.drawable.math,
        "Physics" to Res.drawable.physics,
        "Chemistry" to Res.drawable.chemistry,
        "Biology" to Res.drawable.biology,
        "Computer Studies" to Res.drawable.omputer,
        "Social Studies" to Res.drawable.social_studies,
        "Urdu" to Res.drawable.urdu_icon,
        "Islamiyat" to Res.drawable.islamic,
        "Science" to Res.drawable.biology,
        "General Science" to Res.drawable.social_studies
    )

    val resourceId = subjectImageMap[subjectName] ?: Res.drawable.ic_cyclone
    return painterResource(resourceId)
}