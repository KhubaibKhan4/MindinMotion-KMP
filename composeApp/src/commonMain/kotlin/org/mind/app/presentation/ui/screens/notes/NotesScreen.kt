package org.mind.app.presentation.ui.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.mind.app.domain.model.boards.Boards
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.usecases.ResultState
import org.mind.app.notify
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.utils.Constant.BASE_URL
import kotlin.random.Random

class NotesScreen : Screen {
    @Composable
    override fun Content() {
        NotesScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenContent(
    viewModel: MainViewModel = koinInject(),
) {
    var notesList by remember { mutableStateOf(emptyList<Notes>()) }
    var boardsList by remember { mutableStateOf(emptyList<Boards>()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf(TextFieldValue()) }
    var boardText by remember { mutableStateOf(TextFieldValue()) }

    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
        viewModel.getAllBoards()
    }

    val notesState by viewModel.notes.collectAsState()
    when (notesState) {
        is ResultState.Error -> {
            val error = (notesState as ResultState.Error).message
            ErrorBox(error)
        }

        ResultState.Loading -> {
            LoadingBox()
        }

        is ResultState.Success -> {
            val notes = (notesState as ResultState.Success).data
            notesList = notes.sortedByDescending { it.id }
        }
    }
    val boardState by viewModel.boards.collectAsState()
    when (boardState) {
        is ResultState.Error -> {
            val error = (boardState as ResultState.Error).message
            ErrorBox(error)
        }

        ResultState.Loading -> {
            LoadingBox()
        }

        is ResultState.Success -> {
            val boards = (boardState as ResultState.Success).data
            boardsList = boards.sortedByDescending { it.id }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.EventNote,
                                contentDescription = "Notes"
                            )
                        },
                        text = { Text("Notes", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Article,
                                contentDescription = "Papers"
                            )
                        },
                        text = { Text("Papers", fontWeight = FontWeight.Bold) }
                    )
                }
                if (selectedTabIndex == 0) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text("Search Notes")
                        }
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(128.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val filteredNotes = if (searchText.text.isEmpty()) {
                            notesList
                        } else {
                            notesList.filter { note ->
                                note.title.contains(searchText.text, ignoreCase = true) ||
                                        note.description.contains(
                                            searchText.text,
                                            ignoreCase = true
                                        )
                            }
                        }
                        items(filteredNotes.size) { index ->
                            val note = filteredNotes[index]
                            NoteItem(note)
                        }
                    }
                } else {

                    OutlinedTextField(
                        value = boardText,
                        onValueChange = { boardText = it },
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text("Search Boards")
                        }
                    )
                    val filteredBoards = if (boardText.text.isEmpty()) {
                        boardsList
                    } else {
                        boardsList.filter { board ->
                            board.title.contains(boardText.text, ignoreCase = true) ||
                                    board.description.contains(boardText.text, ignoreCase = true)
                        }
                    }
                    if (filteredBoards.isEmpty()) {
                        Text(
                            "No boards found",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            items(filteredBoards.size) { index ->
                                val board = filteredBoards[index]
                                BoardItem(board)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NoteItem(note: Notes) {
    val navigator = LocalNavigator.current
    var isExpanded by remember { mutableStateOf(false) }
    val randomColor = Color(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = randomColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                if (note.pdfUrl.isEmpty()) {
                    isExpanded = true
                } else {
                    navigator?.push(NotesViewScreen(note))
                }
            }
    ) {
        if (isExpanded) {
            notify("No PDF found")
        }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BoardItem(
    board: Boards,
) {
    val navigator = LocalNavigator.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(6.dp)
                .clickable {
                    navigator?.push(BoardDetailScreen(board))
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val image: io.kamel.core.Resource<Painter> =
                asyncPainterResource(BASE_URL + board.imageUrl)
            KamelImage(
                resource = image,
                contentDescription = "Board Image",
                modifier = Modifier.size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = board.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}