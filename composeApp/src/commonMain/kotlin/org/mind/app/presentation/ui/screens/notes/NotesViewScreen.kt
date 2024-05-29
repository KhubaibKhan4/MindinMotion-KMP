package org.mind.app.presentation.ui.screens.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberSaveableWebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLFile
import org.mind.app.domain.model.notes.Notes
import org.mind.app.providePDF
import org.mind.app.utils.Constant.BASE_URL

class NotesViewScreen(
    private val notes: Notes
) : Screen {
    @Composable
    override fun Content() {
        NotesViewContent(notes)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesViewContent(notes: Notes) {
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text(notes.title)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(top = it.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
           providePDF(BASE_URL+notes.pdfUrl)
        }
    }
}