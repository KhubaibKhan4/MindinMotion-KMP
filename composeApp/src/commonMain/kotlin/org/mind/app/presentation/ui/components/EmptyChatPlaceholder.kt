package org.mind.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.ic_cyclone
import mind_in_motion.composeapp.generated.resources.ic_logo
import org.jetbrains.compose.resources.painterResource
import org.mind.app.theme.LocalThemeIsDark

@Composable
fun EmptyChatPlaceholder() {
    val isDark by LocalThemeIsDark.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_logo),
            contentDescription = "Empty Chat",
            modifier = Modifier.size(120.dp),
        )
        Text(
            text = "No messages yet. Start the conversation!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}