package org.mind.app.presentation.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.mind.app.domain.model.users.Users
import org.mind.app.presentation.ui.components.LocalImage
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

class UserProfileScreen(
    private val users: Users,
) : Screen {
    @Composable
    override fun Content() {
        UserProfileScreenContent(users)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreenContent(user: Users) {
    val isDark by LocalThemeIsDark.current
    val navigator = LocalNavigator.current
    Scaffold(topBar = {
        TopAppBar(title = { Text(text = "User Profile") }, navigationIcon = {
            IconButton(onClick = {
                navigator?.pop()
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Back"
                )
            }
        })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = padding.calculateTopPadding())
                .padding(
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (user.profileImage?.contains("null") == true) {
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                ) {
                    Text(
                        text = user.fullName.take(2).toString(),
                        modifier = Modifier.align(Alignment.Center),
                        color = if (isDark) Color.White else Color.Black,
                        fontSize = 34.sp
                    )
                }
            } else {
                val image: Resource<Painter> = asyncPainterResource(BASE_URL + user.profileImage)
                KamelImage(
                    resource = image,
                    contentDescription = null,
                    modifier = Modifier.size(128.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Text(text = user.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            UserInfoCard(icon = Icons.Filled.Person, label = "Username", info = user.username)
            UserInfoCard(icon = Icons.Filled.Email, label = "Email", info = user.email)
            UserInfoCard(icon = Icons.Filled.Home, label = "Address", info = user.address)
            UserInfoCard(icon = Icons.Filled.LocationCity, label = "City", info = user.city)
            UserInfoCard(icon = Icons.Filled.Flag, label = "Country", info = user.country)
            UserInfoCard(
                icon = Icons.Filled.MarkunreadMailbox,
                label = "Postal Code",
                info = user.postalCode.toString()
            )
            UserInfoCard(icon = Icons.Filled.Phone, label = "Phone", info = user.phoneNumber)
            UserInfoCard(icon = Icons.Filled.Security, label = "Role", info = user.userRole)
        }
    }
}

@Composable
fun UserInfoCard(icon: ImageVector, label: String, info: String) {
    val isDarkTheme by LocalThemeIsDark.current
    val cardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val labelColor = if (isDarkTheme) Color.Gray else Color.DarkGray

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = labelColor,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = info, fontSize = 18.sp, color = textColor, fontWeight = FontWeight.Medium
                )
            }
        }
    }
}