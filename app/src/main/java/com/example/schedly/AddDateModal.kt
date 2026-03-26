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
import java.util.Calendar
import java.util.Locale

@Composable
fun AddDateModal(
    initialDate: Calendar? = null,
    onDismiss: () -> Unit,
    onDateSelected: (Calendar) -> Unit
) {
    var calendar by remember(initialDate) {
        mutableStateOf(initialDate?.copy() ?: Calendar.getInstance())
    }
    var locallySelectedDate by remember(initialDate) {
        mutableStateOf<Calendar?>(initialDate?.copy())
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            DatePickerHeader(
                calendar = calendar,
                onPrevMonth = {
                    calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                },
                onNextMonth = {
                    calendar = (calendar.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            CalendarGrid(
                calendar = calendar,
                selectedDate = locallySelectedDate,
                onDateClick = { locallySelectedDate = it.copy() }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    locallySelectedDate?.copy()?.let { onDateSelected(it) }
                    onDismiss()
                }) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun DatePickerHeader(calendar: Calendar, onPrevMonth: () -> Unit, onNextMonth: () -> Unit) {
    val monthYear = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }
        Text(text = monthYear, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarGrid(calendar: Calendar, selectedDate: Calendar?, onDateClick: (Calendar) -> Unit) {
    val days = listOf("M", "T", "W", "T", "F", "S", "S")
    val dates = getDates(calendar)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            days.forEach { day ->
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFEFF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(dates) { date ->
                if (date == null) {
                    Spacer(modifier = Modifier.size(40.dp))
                } else {
                    val isSelected = selectedDate?.let {
                        it.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                                it.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
                    } ?: false
                    val isCurrentMonth = date.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onDateClick(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.get(Calendar.DAY_OF_MONTH).toString(),
                            color = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                                else -> Color.LightGray
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun Calendar.copy(): Calendar = (this.clone() as Calendar)

fun getDates(calendar: Calendar): List<Calendar?> {
    val dates = mutableListOf<Calendar?>()
    val cal = calendar.copy()
    cal.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val monthStartOffset = (firstDayOfWeek - Calendar.MONDAY + 7) % 7

    cal.add(Calendar.DAY_OF_MONTH, -monthStartOffset)

    repeat(42) {
        dates.add(cal.clone() as Calendar)
        cal.add(Calendar.DAY_OF_MONTH, 1)
    }
    return dates
}