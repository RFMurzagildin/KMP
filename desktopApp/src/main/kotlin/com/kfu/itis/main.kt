package com.kfu.itis

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "WebbySkyList",
        state = WindowState(width = 960.dp, height = 700.dp),
    ) {
        App()
    }
}
