package com.kfu.itis.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kfu.itis.data.model.TaskDto
import com.kfu.itis.stats.StatsContent
import com.kfu.itis.stats.StatsViewModel
import com.kfu.itis.util.toDisplayTime

private enum class TaskTab(val label: String, val icon: String) {
    Today("Сегодня", "⏱"),
    Calendar("Планы", "📅"),
    Stats("Статистика", "📊"),
}

@Composable
fun TodayTasksScreen(
    onLogout: () -> Unit,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    todayViewModel: TasksViewModel = viewModel { TasksViewModel() },
    calendarViewModel: CalendarViewModel = viewModel { CalendarViewModel() },
    statsViewModel: StatsViewModel = viewModel { StatsViewModel() },
) {
    var selectedTab by remember { mutableStateOf(TaskTab.Today) }
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val todayError by todayViewModel.error.collectAsState()
    val calendarError by calendarViewModel.error.collectAsState()
    val statsError by statsViewModel.error.collectAsState()
    val currentError = todayError ?: calendarError ?: statsError

    LaunchedEffect(currentError) {
        if (currentError != null) {
            snackbarHostState.showSnackbar(currentError)
            todayViewModel.clearError()
            calendarViewModel.clearError()
            statsViewModel.clearError()
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == TaskTab.Stats) statsViewModel.loadStats()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                subtitle = when (selectedTab) {
                    TaskTab.Today -> "Задачи на сегодня"
                    TaskTab.Calendar -> "Планы на другие дни"
                    TaskTab.Stats -> "Статистика"
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                showRefresh = selectedTab == TaskTab.Today,
                onRefresh = { todayViewModel.loadTodayTasks() },
                onLogout = onLogout,
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TaskTab.entries.forEach { tab ->
                            val selected = selectedTab == tab
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else Color.Transparent
                                    )
                                    .clickable { selectedTab = tab }
                                    .padding(
                                        horizontal = if (selected) 20.dp else 14.dp,
                                        vertical = 10.dp,
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(tab.icon, style = MaterialTheme.typography.titleMedium)
                                    if (selected) {
                                        Text(
                                            text = tab.label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == TaskTab.Today || selectedTab == TaskTab.Calendar) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Text("+", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Light)
                }
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                TaskTab.Today -> TodayContent(
                    tasks = todayViewModel.tasks.collectAsState().value,
                    isLoading = todayViewModel.isLoading.collectAsState().value,
                    todayDisplay = todayViewModel.todayDisplayString,
                    onDelete = { id -> todayViewModel.deleteTask(id) },
                )
                TaskTab.Calendar -> CalendarContent(viewModel = calendarViewModel)
                TaskTab.Stats -> StatsContent(viewModel = statsViewModel)
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { desc, hour, minute ->
                when (selectedTab) {
                    TaskTab.Today -> todayViewModel.addTask(desc, hour, minute)
                    TaskTab.Calendar -> calendarViewModel.addTask(desc, hour, minute)
                    else -> {}
                }
                showAddDialog = false
            },
        )
    }
}


@Composable
private fun AppTopBar(
    subtitle: String,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    showRefresh: Boolean = false,
    onRefresh: () -> Unit = {},
    onLogout: () -> Unit,
) {
    TopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "WebbySkyList",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        actions = {
            IconButton(onClick = onToggleTheme) {
                Text(
                    text = if (isDarkTheme) "☀️" else "🌙",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            if (showRefresh) {
                IconButton(onClick = onRefresh) {
                    Text("🔄", style = MaterialTheme.typography.titleMedium)
                }
            }
            IconButton(onClick = onLogout) {
                Text("🚪", style = MaterialTheme.typography.titleMedium)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
    )
}

@Composable
private fun TodayContent(
    tasks: List<TaskDto>,
    isLoading: Boolean,
    todayDisplay: String,
    onDelete: (Long) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TodaySummaryBanner(
            taskCount = tasks.size,
            isLoading = isLoading,
            todayDisplay = todayDisplay,
        )
        when {
            isLoading -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            tasks.isEmpty() -> Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                PlaceholderContent("✓", "На сегодня задач нет.\nНажмите + чтобы добавить.")
            }
            else -> LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp, top = 4.dp),
            ) {
                items(tasks, key = { it.id ?: it.hashCode() }) { task ->
                    TaskItemCard(task = task, onDelete = { task.id?.let(onDelete) })
                }
            }
        }
    }
}

@Composable
private fun TodaySummaryBanner(taskCount: Int, isLoading: Boolean, todayDisplay: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = todayDisplay,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
                Text(
                    text = when {
                        isLoading -> "Загружаем задачи..."
                        taskCount == 0 -> "Свободный день ✨"
                        else -> "$taskCount ${taskPluralRu(taskCount)} запланировано"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = when {
                        isLoading -> "⏳"
                        taskCount == 0 -> "🌟"
                        else -> "📋"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    }
}

private fun taskPluralRu(n: Int): String = when {
    n % 100 in 11..19 -> "задач"
    n % 10 == 1 -> "задача"
    n % 10 in 2..4 -> "задачи"
    else -> "задач"
}

@Composable
internal fun TaskItemCard(task: TaskDto, onDelete: () -> Unit) {
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

@Composable
internal fun PlaceholderContent(icon: String, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = icon, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
