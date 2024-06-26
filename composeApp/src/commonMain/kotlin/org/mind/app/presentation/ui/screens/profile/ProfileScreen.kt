package org.mind.app.presentation.ui.screens.profile

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MarkunreadMailbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.style.TextOverflow
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
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.mind.app.createTempFileFromBitmap
import org.mind.app.domain.model.users.Users
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.ErrorBox
import org.mind.app.presentation.ui.components.LoadingBox
import org.mind.app.presentation.ui.screens.setting.SettingScreen
import org.mind.app.presentation.viewmodel.MainViewModel
import org.mind.app.theme.LocalThemeIsDark
import org.mind.app.utils.Constant.BASE_URL

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
        var uploadResult by remember { mutableStateOf<String?>(null) }
        var email by remember { mutableStateOf("") }
        LaunchedEffect(isLogin) {
            isLogin = preference.getBoolean("is_login", false)
            email = preference.getString("email").toString()
        }
        LaunchedEffect(Unit) {
            viewModel.getUserByEmail(email)
        }

        val userByEmailState by viewModel.userByEmail.collectAsState()
        val uploadState by viewModel.uploadImage.collectAsState()
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
        when (uploadState) {
            is ResultState.Error -> {
                val error = (uploadState as ResultState.Error).message
                ErrorBox(error)
            }

            ResultState.Loading -> {
                //LoadingBox()
            }

            is ResultState.Success -> {
                val response = (uploadState as ResultState.Success).data
                uploadResult = response
            }
        }
        val scope = rememberCoroutineScope()
        var images by remember { mutableStateOf<ImageBitmap?>(null) }
        val singleImagePicker = rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { byteArrays ->
                byteArrays.firstOrNull()?.let { byteArray ->
                    if (byteArray.isNotEmpty()) {
                        usersDetails?.let {
                            viewModel.uploadProfileImage(it.id, imageFile = byteArray)
                        }
                    }
                    images = byteArray.toImageBitmap()
                    scope.launch {
                        val file = createTempFileFromBitmap(byteArray.toImageBitmap())
                        viewModel.uploadImageAndGetUrl(file, email)
                    }
                }
            },
        )
        Scaffold(topBar = {
            TopAppBar(title = { Text("Profile") }, actions = {
                Icon(imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.clickable {
                        usersDetails?.let {
                            navigator?.push(SettingScreen(it))
                        }
                    })
            })
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
                        Box {
                            if (images != null) {
                                Image(
                                    bitmap = images!!,
                                    contentDescription = "frame",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(150.dp).clip(CircleShape)
                                        .clickable { singleImagePicker.launch() },
                                )
                            } else {
                                if (usersDetails?.profileImage != "null") {
                                    val image: Resource<Painter> =
                                        asyncPainterResource(BASE_URL + usersDetails?.profileImage.toString())
                                    KamelImage(
                                        resource = image,
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Gray,
                                                shape = CircleShape
                                            ),
                                        contentScale = ContentScale.Crop,
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceContainer)
                                            .border(
                                                width = 1.dp,
                                                color = Color.Gray,
                                                shape = CircleShape
                                            )
                                    ) {
                                        Text(
                                            text = usersDetails?.fullName?.take(2).toString(),
                                            modifier = Modifier.align(Alignment.Center),
                                            color = if (isDark) Color.White else Color.Black,
                                            fontSize = 34.sp
                                        )
                                    }
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(30.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-15).dp, y = 2.dp)
                                    .background(Color.White, shape = CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                                    .clickable { singleImagePicker.launch() }
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UserInfoCard(
                                icon = Icons.Filled.Person,
                                label = "Username",
                                info = usersDetails?.username.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.Email,
                                label = "Email",
                                info = usersDetails?.email.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.Home,
                                label = "Address",
                                info = usersDetails?.address.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.LocationCity,
                                label = "City",
                                info = usersDetails?.city.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.Flag,
                                label = "Country",
                                info = usersDetails?.country.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.MarkunreadMailbox,
                                label = "Postal Code",
                                info = usersDetails?.postalCode.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.Phone,
                                label = "Phone",
                                info = usersDetails?.phoneNumber.toString()
                            )
                            UserInfoCard(
                                icon = Icons.Filled.Security,
                                label = "Role",
                                info = usersDetails?.userRole.toString()
                            )
                        }
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

@Composable
fun ProfileDetails(user: Users) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        DetailCard(
            title = "Username", name = user.username
        )
        DetailCard(
            title = "Address", name = user.address
        )
        DetailCard(
            title = "City", name = user.city
        )
        DetailCard(
            title = "Country", name = user.country
        )
        DetailCard(
            title = "Postal Code", name = user.postalCode.toString()
        )
        DetailCard(
            title = "Phone Number", name = user.phoneNumber
        )
        DetailCard(
            title = "Role", name = user.userRole
        )
    }
}

@Composable
fun DetailCard(
    title: String,
    name: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
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
