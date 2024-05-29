package org.mind.app.presentation.ui.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.compose.koinInject
import org.mind.app.domain.model.notes.Notes
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.viewmodel.MainViewModel

class NotesScreen : Screen {
    @Composable
    override fun Content() {
        NotesScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreenContent(
    viewModel: MainViewModel= koinInject()
) {
    var notesList by remember { mutableStateOf(emptyList<Notes>()) }
    LaunchedEffect(Unit){
        viewModel.getAllNotes()
    }
    val notesState by viewModel.notes.collectAsState()
    when(notesState){
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
           items(notesList){note ->
               NoteItem(note)
           }
        }
    }
}
@Composable
fun NoteItem(note: Notes) {
    val navigator = LocalNavigator.current
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation =CardDefaults.cardElevation( 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigator?.push(NotesViewScreen(note)) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}