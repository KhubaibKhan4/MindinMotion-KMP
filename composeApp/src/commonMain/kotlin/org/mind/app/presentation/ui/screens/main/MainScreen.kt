package org.mind.app.presentation.ui.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.cmppreference.LocalPreference
import com.example.cmppreference.LocalPreferenceProvider
import org.mind.app.presentation.ui.components.TabItem
import org.mind.app.presentation.ui.tabs.Instructors.InstructorsTab
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.ui.tabs.shop.ShopTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        LocalPreferenceProvider {
            val preference = LocalPreference.current
            val isLoggedIn by remember { mutableStateOf(preference.getBoolean("is_login", false)) }
            if (isLoggedIn) {
                TabNavigator(
                    tab = HomeTab,
                    disposeNestedNavigators = true,
                ) { tabNavigator ->
                    Scaffold(bottomBar = {
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
    }
}