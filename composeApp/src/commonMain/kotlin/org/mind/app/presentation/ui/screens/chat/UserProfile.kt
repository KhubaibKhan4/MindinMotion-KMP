package org.mind.app.presentation.ui.screens.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.seiko.imageloader.rememberImagePainter
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.components.LocalImage
import org.mind.app.presentation.ui.screens.profile.ProfileDetails
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

class UserProfile : Screen {

    @Composable
    override fun Content() {
        UserProfileScreenContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreenContent(
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
        LaunchedEffect(Unit) {
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
        val scope = rememberCoroutineScope()
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let { byteArray ->
                    images = byteArray.toImageBitmap()
                    scope.launch {}
                }
            },
        )


        Scaffold(topBar = {
            TopAppBar(title = { Text("Profile") },
                navigationIcon = {
                    Icon(imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navigator?.pop()
                        }
                    )
                }
            )
        }) {

            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = it.calculateTopPadding())
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (usersDetails != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        if (usersDetails?.profileImage != "null") {
                            val image: Resource<Painter> = asyncPainterResource(BASE_URL+ usersDetails?.profileImage.toString())
                            KamelImage(
                                resource = image,
                                contentDescription = null,
                                modifier = Modifier.size(150.dp).clip(CircleShape)
                            )
                        } else {
                            if (images != null) {
                                Image(
                                    bitmap = images!!,
                                    contentDescription = "frame",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(150.dp).clip(CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .border(width = 1.dp, color = Color.Gray, shape = CircleShape)
                                ) {
                                    Text(
                                        text = usersDetails?.fullName?.first().toString(),
                                        modifier = Modifier.align(Alignment.Center),
                                        color = if (isDark) Color.White else Color.Black,
                                        fontSize = 24.sp
                                    )
                                }
                            }
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
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text("No User Data is Found!")
                    }
                }
            }
        }
    }
}