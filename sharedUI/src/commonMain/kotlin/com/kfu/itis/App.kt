package com.kfu.itis

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kfu.itis.auth.LoginScreen
import com.kfu.itis.auth.RegisterScreen
import com.kfu.itis.session.SessionManager
import com.kfu.itis.tasks.TodayTasksScreen
import com.kfu.itis.theme.AppTheme

private sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object Tasks : Screen()
}

@Composable
fun App(darkTheme: Boolean = isSystemInDarkTheme()) {
    var isDark by remember { mutableStateOf(darkTheme) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

    AppTheme(darkTheme = isDark) {
        when (currentScreen) {
            is Screen.Login -> LoginScreen(
                onLoginSuccess = { currentScreen = Screen.Tasks },
                onNavigateToRegister = { currentScreen = Screen.Register },
                isDarkTheme = isDark,
                onToggleTheme = { isDark = !isDark },
            )
            is Screen.Register -> RegisterScreen(
                onRegisterSuccess = { currentScreen = Screen.Login },
                onNavigateToLogin = { currentScreen = Screen.Login },
                isDarkTheme = isDark,
                onToggleTheme = { isDark = !isDark },
            )
            is Screen.Tasks -> TodayTasksScreen(
                onLogout = {
                    SessionManager.logout()
                    currentScreen = Screen.Login
                },
                isDarkTheme = isDark,
                onToggleTheme = { isDark = !isDark },
            )
        }
    }
}
