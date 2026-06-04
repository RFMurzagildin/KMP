package com.kfu.itis.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kfu.itis.components.AppButton
import com.kfu.itis.components.AppTextField

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (description: String, hour: Int, minute: Int) -> Unit,
) {
    var description by remember { mutableStateOf("") }
    var hourInput by remember { mutableStateOf("") }
    var minuteInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        if (description.isBlank()) {
            validationError = "Введите описание задачи"
            return false
        }
        val h = hourInput.toIntOrNull()
        val m = minuteInput.toIntOrNull()
        if (h == null || h !in 0..23) {
            validationError = "Часы: 0–23"
            return false
        }
        if (m == null || m !in 0..59) {
            validationError = "Минуты: 0–59"
            return false
        }
        validationError = null
        return true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Новая задача",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        validationError = null
                    },
                    label = "Описание задачи",
                    singleLine = false,
                )

                Text(
                    text = "Время",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    AppTextField(
                        value = hourInput,
                        onValueChange = { if (it.length <= 2) hourInput = it.filter { c -> c.isDigit() } },
                        label = "ЧЧ",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                    )
                    Text(
                        text = " : ",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                    AppTextField(
                        value = minuteInput,
                        onValueChange = { if (it.length <= 2) minuteInput = it.filter { c -> c.isDigit() } },
                        label = "ММ",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(80.dp),
                    )
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        },
        confirmButton = {
            AppButton(
                text = "Добавить",
                onClick = {
                    if (validate()) {
                        onConfirm(description.trim(), hourInput.toInt(), minuteInput.toInt())
                    }
                },
                modifier = Modifier.fillMaxWidth(0.5f),
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}
