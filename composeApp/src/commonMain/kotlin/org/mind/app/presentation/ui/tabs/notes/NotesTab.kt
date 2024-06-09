import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.mind.app.presentation.ui.screens.notes.NotesScreen

object NotesTab : Tab {
    @Composable
    override fun Content() {
        Navigator(NotesScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val title by remember { mutableStateOf("Notes") }
            val icon = rememberVectorPainter(Icons.AutoMirrored.Filled.EventNote)
            val index: UShort = 2u
            return TabOptions(index, title, icon)
        }
}