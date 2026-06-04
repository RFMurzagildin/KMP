package com.kfu.itis.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfu.itis.data.repository.TaskRepository
import com.kfu.itis.util.getDayOfWeekShort
import com.kfu.itis.util.subtractDays
import com.kfu.itis.util.todayDateString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DayCount(val label: String, val count: Int)

data class StatsData(
    val totalTasks: Int,
    val todayTasks: Int,
    val busiestDay: String,
    val peakTime: String,
    val last7Days: List<DayCount>,
    val morningCount: Int,
    val dayCount: Int,
    val eveningCount: Int,
    val nightCount: Int,
)

class StatsViewModel : ViewModel() {

    private val taskRepository = TaskRepository()

    private val _stats = MutableStateFlow<StatsData?>(null)
    val stats: StateFlow<StatsData?> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            taskRepository.getAllTasks()
                .onSuccess { tasks ->
                    val today = todayDateString()

                    val totalTasks = tasks.size
                    val todayTasks = tasks.count { it.time.startsWith(today) }

                    val last7Days = (6 downTo 0).map { offset ->
                        val date = if (offset == 0) today else subtractDays(today, offset)
                        val count = tasks.count { it.time.startsWith(date) }
                        val y = date.substring(0, 4).toInt()
                        val m = date.substring(5, 7).toInt()
                        val d = date.substring(8, 10).toInt()
                        DayCount(label = getDayOfWeekShort(y, m, d), count = count)
                    }

                    var morning = 0; var day = 0; var evening = 0; var night = 0
                    tasks.forEach { task ->
                        val timePart = task.time.substringAfter('T', "00:00:00")
                        val hour = timePart.substringBefore(':').toIntOrNull() ?: 0
                        when {
                            hour in 6..11 -> morning++
                            hour in 12..17 -> day++
                            hour in 18..22 -> evening++
                            else -> night++
                        }
                    }

                    val peakTime = listOf(
                        "Утро (6–12)" to morning,
                        "День (12–18)" to day,
                        "Вечер (18–23)" to evening,
                        "Ночь (23–6)" to night,
                    ).maxByOrNull { it.second }?.first ?: "—"

                    val dowCounts = IntArray(7)
                    tasks.forEach { task ->
                        val date = task.time.substringBefore('T')
                        if (date.length == 10) {
                            val y = date.substring(0, 4).toInt()
                            val m = date.substring(5, 7).toInt()
                            val d = date.substring(8, 10).toInt()
                            val dow = getDayOfWeekShort(y, m, d)
                            val idx = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").indexOf(dow)
                            if (idx >= 0) dowCounts[idx]++
                        }
                    }
                    val dowNames = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
                    val busiestDay = if (dowCounts.all { it == 0 }) "—"
                        else dowNames[dowCounts.indices.maxByOrNull { dowCounts[it] }!!]

                    _stats.value = StatsData(
                        totalTasks = totalTasks,
                        todayTasks = todayTasks,
                        busiestDay = busiestDay,
                        peakTime = peakTime,
                        last7Days = last7Days,
                        morningCount = morning,
                        dayCount = day,
                        eveningCount = evening,
                        nightCount = night,
                    )
                }
                .onFailure { e -> _error.value = e.message }
            _isLoading.value = false
        }
    }

    fun clearError() { _error.value = null }
}
