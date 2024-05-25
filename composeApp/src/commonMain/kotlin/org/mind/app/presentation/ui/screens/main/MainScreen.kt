package org.mind.app.presentation.ui.screens.main

import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.koin.compose.koinInject
import org.mind.app.domain.usecases.ResultState
import org.mind.app.presentation.ui.components.TabItem
import org.mind.app.presentation.ui.screens.auth.login.LoginScreen
import org.mind.app.presentation.ui.tabs.Instructors.InstructorsTab
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.ui.tabs.shop.ShopTab
import org.mind.app.presentation.viewmodel.MainViewModel
import kotlin.random.Random

object MainScreen : Tab {
    override val key: ScreenKey =
        super.key + "${Random.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)}"

    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            TabNavigator(
                tab = HomeTab,
            ) { tabNavigator ->
                Scaffold(bottomBar = {
                    if (tabNavigator.current != LoginScreen) {
                        NavigationBar(
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color.White,
                            contentColor = contentColorFor(Color.Red),
                            tonalElevation = 8.dp,
                            windowInsets = WindowInsets.ime
                        ) {
                            TabItem(HomeTab)
                            TabItem(InstructorsTab)
                            TabItem(ShopTab)
                            TabItem(ProfileTab)
                        }
                    }
                }) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(
                            bottom = it.calculateBottomPadding(),
                            start = 0.dp
                        )
                    ) {
                        CurrentTab()
                    }
                }
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Menu)
            val title = "Main"
            val index: UShort = 5u
            return TabOptions(index, title, icon)
        }
}