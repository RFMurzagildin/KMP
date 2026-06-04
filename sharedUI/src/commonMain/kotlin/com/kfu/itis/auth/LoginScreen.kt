package com.kfu.itis.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kfu.itis.components.AppButton
import com.kfu.itis.components.AppPasswordField
import com.kfu.itis.components.AppTextField
import com.kfu.itis.components.AppTextButton

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    viewModel: AuthViewModel = viewModel { AuthViewModel() },
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.loginState.collectAsState()
    val isLoading = state is AuthUiState.Loading
    val errorMessage = (state as? AuthUiState.Error)?.message

    LaunchedEffect(state) {
        if (state is AuthUiState.Success) {
            viewModel.resetLoginState()
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 420.dp)
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Theme toggle aligned to the end
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(onClick = onToggleTheme) {
                        Text(
                            text = if (isDarkTheme) "☀️" else "🌙",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                BrandHeader(tagline = "С возвращением! Войдите в аккаунт")

                Spacer(modifier = Modifier.height(4.dp))

                AppTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (state is AuthUiState.Error) viewModel.resetLoginState()
                    },
                    label = "Имя пользователя",
                )

                AppPasswordField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (state is AuthUiState.Error) viewModel.resetLoginState()
                    },
                    label = "Пароль",
                )

                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AppButton(
                    text = "Войти",
                    onClick = { viewModel.login(username, password) },
                    isLoading = isLoading,
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Нет аккаунта?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AppTextButton(
                        text = "Зарегистрироваться",
                        onClick = onNavigateToRegister,
                    )
                }
            }
        }
    }
}
