package com.kfu.itis.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kfu.itis.data.model.TaskDto
import com.kfu.itis.util.daysInMonth
import com.kfu.itis.util.firstDayOfMonth
import com.kfu.itis.util.formatDateString
import com.kfu.itis.util.monthName
import com.kfu.itis.util.toDisplayTime
import com.kfu.itis.util.todayDateString

@Composable
fun CalendarContent(viewModel: CalendarViewModel) {
    val year by viewModel.displayYear.collectAsState()
    val month by viewModel.displayMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val today = todayDateString()

    if (year == 0 || month == 0) return

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth >= 600.dp) {

            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { viewModel.prevMonth() }) {
                            Text("‹", style = MaterialTheme.typography.headlineMedium)
                        }
                        Text(
                            text = "${monthName(month)} $year",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        IconButton(onClick = { viewModel.nextMonth() }) {
                            Text("›", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CalendarGrid(
                        year = year, month = month,
                        selectedDate = selectedDate, todayString = today,
                        onDayClick = { date -> viewModel.selectDate(date) },
                    )
                }

                VerticalDivider(modifier = Modifier.fillMaxHeight())

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(
                        text = "Задачи на ${viewModel.selectedDateDisplay}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    TasksPanel(tasks = tasks, isLoading = isLoading, onDelete = { viewModel.deleteTask(it) })
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { viewModel.prevMonth() }) {
                        Text("‹", style = MaterialTheme.typography.headlineMedium)
                    }
                    Text(
                        text = "${monthName(month)} $year",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Text("›", style = MaterialTheme.typography.headlineMedium)
                    }
                }

                CalendarGrid(
                    year = year, month = month,
                    selectedDate = selectedDate, todayString = today,
                    onDayClick = { date -> viewModel.selectDate(date) },
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Задачи на ${viewModel.selectedDateDisplay}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))

                TasksPanel(
                    tasks = tasks,
                    isLoading = isLoading,
                    onDelete = { viewModel.deleteTask(it) },
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun TasksPanel(
    tasks: List<TaskDto>,
    isLoading: Boolean,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        isLoading -> Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        tasks.isEmpty() -> Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✓", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нет задач на этот день.\nНажмите + чтобы добавить.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        else -> LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp, top = 4.dp),
        ) {
            items(tasks, key = { it.id ?: it.hashCode() }) { task ->
                CalendarTaskItem(
                    task = task,
                    onDelete = { task.id?.let { onDelete(it) } },
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDate: String,
    todayString: String,
    onDayClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val days = daysInMonth(year, month)
    val firstDay = firstDayOfMonth(year, month)

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        val cells = buildList {
            repeat(firstDay) { add(0) }
            for (d in 1..days) add(d)
            while (size % 7 != 0) add(0)
        }

        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (day > 0) {
                            val dateStr = formatDateString(year, month, day)
                            DayCell(
                                day = day,
                                isSelected = dateStr == selectedDate,
                                isToday = dateStr == todayString,
                                onClick = { onDayClick(dateStr) },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val weight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bgColor)
            .then(
                if (isToday && !isSelected)
                    Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = weight,
        )
    }
}

@Composable
private fun CalendarTaskItem(task: TaskDto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏰", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.time.toDisplayTime(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Text("🗑", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
