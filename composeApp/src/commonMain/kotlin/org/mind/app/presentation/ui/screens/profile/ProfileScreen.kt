package org.mind.app.presentation.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import com.seiko.imageloader.rememberImagePainter
import org.koin.compose.koinInject
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.components.LocalImage
import org.mind.app.presentation.ui.screens.setting.SettingScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        ProfileScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    viewModel: MainViewModel = koinInject(),
) {
    LocalPreferenceProvider {
        val preference = LocalPreference.current
        val tabNavigator = LocalTabNavigator.current
        val isDark by LocalThemeIsDark.current
        val navigator = LocalNavigator.current
        var isLogin by remember { mutableStateOf(false) }
        var usersDetails by remember { mutableStateOf<Users?>(null) }
        var email by remember { mutableStateOf("") }
        LaunchedEffect(isLogin) {
            isLogin = preference.getBoolean("is_login", false)
            email = preference.getString("email").toString()
        }
        LaunchedEffect(Unit){
            viewModel.getUserByEmail(email)
        }

        val userByEmailState by viewModel.userByEmail.collectAsState()
        when (userByEmailState) {
            is ResultState.Error -> {
                val error = (userByEmailState as ResultState.Error).message
                ErrorBox(error)
            }

            ResultState.Loading -> {
                LoadingBox()
            }

            is ResultState.Success -> {
                val response = (userByEmailState as ResultState.Success).data
                usersDetails = response
            }
        }


        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Profile") },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                usersDetails?.let {
                                    navigator?.push(SettingScreen(it))
                                }
                            }
                        )
                    }
                )
            }
        ) {
            LazyColumn {
                item {
                    if (usersDetails != null) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(top = it.calculateTopPadding()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            if (usersDetails?.profileImage != "null") {
                                Image(
                                    painter = rememberImagePainter(usersDetails?.profileImage.toString()),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                LocalImage(
                                    modifier = Modifier.size(150.dp).clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = usersDetails?.fullName.toString(),
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                    color = if (isDark) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = usersDetails?.email.toString(),
                                    fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            usersDetails?.let { it1 -> ProfileDetails(it1) }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No User Data is Found!")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetails(user: Users) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        DetailCard(
            title = "Username",
            name = user.username
        )
        DetailCard(
            title = "Address",
            name = user.address
        )
        DetailCard(
            title = "City",
            name = user.city
        )
        DetailCard(
            title = "Country",
            name = user.country
        )
        DetailCard(
            title = "Postal Code",
            name = user.postalCode.toString()
        )
        DetailCard(
            title = "Phone Number",
            name = user.phoneNumber
        )
        DetailCard(
            title = "Role",
            name = user.userRole
        )
    }
}

@Composable
fun DetailCard(
    title: String,
    name: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = name,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
