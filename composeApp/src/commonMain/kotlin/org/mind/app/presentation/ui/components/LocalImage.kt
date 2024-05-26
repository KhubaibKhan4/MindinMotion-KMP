package org.mind.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import mind_in_motion.composeapp.generated.resources.Res
import mind_in_motion.composeapp.generated.resources.avatar
import org.jetbrains.compose.resources.painterResource

@Composable
fun LocalImage(modifier: Modifier) {
    Image(
        painter = painterResource(Res.drawable.avatar),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}