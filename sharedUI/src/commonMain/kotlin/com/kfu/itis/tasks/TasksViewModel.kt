package com.kfu.itis.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfu.itis.data.model.TaskDto
import com.kfu.itis.data.repository.TaskRepository
import com.kfu.itis.util.todayDateString
import com.kfu.itis.util.toDisplayDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val todayString: String get() = todayDateString()

    val todayDisplayString: String get() = todayString.toDisplayDate()

    init {
        loadTodayTasks()
    }

    fun loadTodayTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            taskRepository.getTasksByDate(todayString)
                .onSuccess { list ->
                    _tasks.value = list.sortedBy { it.time }
                }
                .onFailure { e ->
                    _error.value = e.message
                }
            _isLoading.value = false
        }
    }

    fun addTask(description: String, hour: Int, minute: Int) {
        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        val timeString = "${todayString}T${h}:${m}:00"
        viewModelScope.launch {
            taskRepository.createTask(TaskDto(description = description, time = timeString))
                .onSuccess { loadTodayTasks() }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId)
                .onSuccess { loadTodayTasks() }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
