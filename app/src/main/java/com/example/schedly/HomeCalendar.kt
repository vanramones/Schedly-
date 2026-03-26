    package com.example.schedly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeCalendarModal(
    initialDate: LocalDate?,
    scheduledDates: Set<LocalDate>,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(initialDate?.let { YearMonth.from(it) } ?: YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DatePickerHeader(
                currentMonth = currentMonth,
                onPrevMonth = { currentMonth = currentMonth.minusMonths(1) },
                onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CalendarGrid(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                scheduledDates = scheduledDates,
                onDateClick = { selectedDate = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = {
                        selectedDate?.let { onDateSelected(it) }
                        onDismiss()
                    },
                    enabled = selectedDate != null
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun DatePickerHeader(currentMonth: YearMonth, onPrevMonth: () -> Unit, onNextMonth: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(text = currentMonth.format(formatter), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate?,
    scheduledDates: Set<LocalDate>,
    onDateClick: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
    val dates = generateDatesForMonth(currentMonth)

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            daysOfWeek.forEach { day ->
                Text(text = day, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(dates) { date ->
                val isSelected = selectedDate == date
                val isCurrentMonth = YearMonth.from(date) == currentMonth
                val hasSchedules = scheduledDates.contains(date)
                val highlightColor = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    hasSchedules -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(highlightColor)
                        .clickable { onDateClick(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            hasSchedules && isCurrentMonth -> MaterialTheme.colorScheme.primary
                            isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                            else -> Color.LightGray
                        }
                    )
                }
            }
        }
    }
}

private fun generateDatesForMonth(month: YearMonth): List<LocalDate> {
    val dates = mutableListOf<LocalDate>()
    val firstDayOfMonth = month.atDay(1)
    val daysInWeek = 7
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // Monday is 1, Sunday is 7
    val daysToPrecede = (firstDayOfWeek - DayOfWeek.MONDAY.value + daysInWeek) % daysInWeek

    val startDate = firstDayOfMonth.minusDays(daysToPrecede.toLong())

    var currentDate = startDate
    repeat(42) { // 6 weeks
        dates.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }
    return dates
}
