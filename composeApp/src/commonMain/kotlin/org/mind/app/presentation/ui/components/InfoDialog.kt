package org.mind.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun InfoDialog(
    onCloseClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onCloseClicked() },
        title = {
            Text(
                text = "Important Information",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "This Chat Bot can make mistakes. Please keep this in mind while using it.",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Icon(
                Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onCloseClicked()
                }
            )
        },
        modifier = Modifier.padding(8.dp)
    )
}