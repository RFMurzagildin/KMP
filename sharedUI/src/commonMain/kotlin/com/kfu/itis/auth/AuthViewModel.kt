package com.kfu.itis.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfu.itis.data.repository.AuthRepository
import com.kfu.itis.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _loginState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val loginState: StateFlow<AuthUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val registerState: StateFlow<AuthUiState> = _registerState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginState.value = AuthUiState.Error("Заполните все поля")
            return
        }
        viewModelScope.launch {
            _loginState.value = AuthUiState.Loading
            authRepository.login(username, password)
                .onSuccess { token ->
                    SessionManager.authToken = token
                    _loginState.value = AuthUiState.Success
                }
                .onFailure { e ->
                    _loginState.value = AuthUiState.Error(e.message ?: "Неизвестная ошибка")
                }
        }
    }

    fun register(username: String, password: String, confirmPassword: String) {
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _registerState.value = AuthUiState.Error("Заполните все поля")
            return
        }
        if (password != confirmPassword) {
            _registerState.value = AuthUiState.Error("Пароли не совпадают")
            return
        }
        if (password.length < 6) {
            _registerState.value = AuthUiState.Error("Пароль должен быть не менее 6 символов")
            return
        }
        viewModelScope.launch {
            _registerState.value = AuthUiState.Loading
            authRepository.register(username, password, confirmPassword)
                .onSuccess { response ->
                    if (response.success) {
                        _registerState.value = AuthUiState.Success
                    } else {
                        _registerState.value = AuthUiState.Error(
                            response.message ?: "Ошибка регистрации"
                        )
                    }
                }
                .onFailure { e ->
                    _registerState.value = AuthUiState.Error(e.message ?: "Неизвестная ошибка")
                }
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthUiState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthUiState.Idle
    }
}
