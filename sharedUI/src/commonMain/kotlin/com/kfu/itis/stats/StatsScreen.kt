package com.kfu.itis.stats

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun StatsContent(viewModel: StatsViewModel) {
    val stats by viewModel.stats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    when {
        isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        stats == null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Нет данных",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        else -> {
            val data = stats!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(modifier = Modifier.weight(1f), icon = "📋", label = "Всего задач", value = data.totalTasks.toString())
                    StatCard(modifier = Modifier.weight(1f), icon = "📅", label = "Сегодня", value = data.todayTasks.toString())
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StatCard(modifier = Modifier.weight(1f), icon = "🔥", label = "Топ день недели", value = data.busiestDay)
                    StatCard(modifier = Modifier.weight(1f), icon = "⏰", label = "Пик активности", value = data.peakTime)
                }

                SectionTitle("Задачи за последние 7 дней")
                BarChart(days = data.last7Days)

                SectionTitle("Распределение по времени суток")
                val timeSlots = listOf(
                    "Утро (6–12)" to data.morningCount,
                    "День (12–18)" to data.dayCount,
                    "Вечер (18–23)" to data.eveningCount,
                    "Ночь (23–6)" to data.nightCount,
                )
                val maxTime = timeSlots.maxOfOrNull { it.second }.takeIf { it != null && it > 0 } ?: 1
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        timeSlots.forEachIndexed { index, (label, count) ->
                            HorizontalBar(label = label, count = count, maxCount = maxTime)
                            if (index < timeSlots.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: String, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(icon, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun BarChart(days: List<DayCount>) {
    val maxCount = days.maxOfOrNull { it.count }.takeIf { it != null && it > 0 } ?: 1
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            days.forEach { (label, count) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    if (count > 0) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    val barHeight = (120 * count / maxCount).coerceAtLeast(if (count > 0) 4 else 2)
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(barHeight.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (count > 0) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun HorizontalBar(label: String, count: Int, maxCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.width(110.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(8.dp))
        val fraction = count.toFloat() / maxCount.toFloat()
        Box(
            modifier = Modifier
                .weight(1f)
                .height(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant),
        ) {
            if (fraction > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = count.toString(),
            modifier = Modifier.width(24.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
        )
    }
}
