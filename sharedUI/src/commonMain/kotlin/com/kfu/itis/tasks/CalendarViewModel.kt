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

class CalendarViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _displayYear = MutableStateFlow(0)
    val displayYear: StateFlow<Int> = _displayYear.asStateFlow()

    private val _displayMonth = MutableStateFlow(0)
    val displayMonth: StateFlow<Int> = _displayMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow("")
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val selectedDateDisplay: String get() = _selectedDate.value.toDisplayDate()

    private val _tasks = MutableStateFlow<List<TaskDto>>(emptyList())
    val tasks: StateFlow<List<TaskDto>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        val today = todayDateString()
        _selectedDate.value = today
        _displayYear.value = today.substring(0, 4).toInt()
        _displayMonth.value = today.substring(5, 7).toInt()
        loadTasksForDate(today)
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        loadTasksForDate(date)
    }

    fun nextMonth() {
        val m = _displayMonth.value
        val y = _displayYear.value
        if (m == 12) { _displayMonth.value = 1; _displayYear.value = y + 1 }
        else _displayMonth.value = m + 1
    }

    fun prevMonth() {
        val m = _displayMonth.value
        val y = _displayYear.value
        if (m == 1) { _displayMonth.value = 12; _displayYear.value = y - 1 }
        else _displayMonth.value = m - 1
    }

    fun loadTasksForDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            taskRepository.getTasksByDate(date)
                .onSuccess { list -> _tasks.value = list.sortedBy { it.time } }
                .onFailure { e -> _error.value = e.message }
            _isLoading.value = false
        }
    }

    fun addTask(description: String, hour: Int, minute: Int) {
        val date = _selectedDate.value
        val h = hour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        viewModelScope.launch {
            taskRepository.createTask(TaskDto(description = description, time = "${date}T${h}:${m}:00"))
                .onSuccess { loadTasksForDate(date) }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val date = _selectedDate.value
            taskRepository.deleteTask(taskId)
                .onSuccess { loadTasksForDate(date) }
                .onFailure { e -> _error.value = e.message }
        }
    }

    fun clearError() { _error.value = null }
}
