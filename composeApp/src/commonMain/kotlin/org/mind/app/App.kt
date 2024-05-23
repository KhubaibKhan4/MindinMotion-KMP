package org.mind.app

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import org.mind.app.presentation.ui.tabs.Instructors.InstructorsTab
import org.mind.app.presentation.ui.tabs.home.HomeTab
import org.mind.app.presentation.ui.tabs.profile.ProfileTab
import org.mind.app.presentation.ui.tabs.shop.ShopTab
import org.mind.app.theme.AppTheme
import org.mind.app.theme.LocalThemeIsDark

@Composable
internal fun App() = AppTheme {
    TabNavigator(
        HomeTab,
        disposeNestedNavigators = false,
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

@Composable
fun RowScope.TabItem(tab: Tab) {
    val isDark by LocalThemeIsDark.current
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .height(58.dp).clip(RoundedCornerShape(16.dp)),
        selected = tabNavigator.current == tab,
        onClick = {
            tabNavigator.current = tab
        },
        icon = {
            tab.options.icon?.let { painter ->
                Icon(
                    painter,
                    contentDescription = tab.options.title,
                    tint = if (tabNavigator.current == tab) Color.Red else if (isDark) Color.White else Color.Black
                )
            }
        },
        label = {
            tab.options.title.let { title ->
                Text(
                    title,
                    fontSize = 12.sp,
                    color = if (tabNavigator.current == tab) Color.Red else if (isDark) Color.White else Color.Black
                )
            }
        },
        enabled = true,
        alwaysShowLabel = true,
        interactionSource = MutableInteractionSource(),
        colors = NavigationBarItemDefaults.colors(
            indicatorColor = Color.Transparent,
            selectedIconColor = Color.Red,
            unselectedIconColor = Color.Black,
        )
    )
}


internal expect fun openUrl(url: String?)