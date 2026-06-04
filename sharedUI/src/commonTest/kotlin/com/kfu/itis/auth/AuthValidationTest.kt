package com.kfu.itis.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AuthValidationTest {

    @Test
    fun `login - blank username sets error state`() {
        val vm = AuthViewModel()
        vm.login(username = "", password = "secret123")
        val state = assertIs<AuthUiState.Error>(vm.loginState.value)
        assertEquals("Заполните все поля", state.message)
    }

    @Test
    fun `login - blank password sets error state`() {
        val vm = AuthViewModel()
        vm.login(username = "alice", password = "")
        val state = assertIs<AuthUiState.Error>(vm.loginState.value)
        assertEquals("Заполните все поля", state.message)
    }

    @Test
    fun `login - both fields blank sets error state`() {
        val vm = AuthViewModel()
        vm.login(username = "", password = "")
        assertIs<AuthUiState.Error>(vm.loginState.value)
    }

}
