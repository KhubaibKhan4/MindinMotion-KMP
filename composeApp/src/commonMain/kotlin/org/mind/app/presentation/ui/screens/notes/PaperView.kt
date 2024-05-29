package org.mind.app.presentation.ui.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.mind.app.domain.model.papers.Paper
import org.mind.app.providePDF
import org.mind.app.utils.Constant

class PaperViewScreen(
    private val papers: Paper,
) : Screen {
    @Composable
    override fun Content() {
        PaperViewContent(papers)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaperViewContent(papers: Paper) {
    val navigator = LocalNavigator.current
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text("")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    )
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
            providePDF(Constant.BASE_URL + papers.pdfUrl)
        }
    }
}