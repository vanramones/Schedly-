package com.example.schedly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.schedly.ui.theme.SchedlyTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun EditScheduleModal(
    schedule: Schedule,
    onDismiss: () -> Unit,
    onDone: (Schedule) -> Unit,
    onDelete: (Schedule) -> Unit
) {
    var scheduleName by remember(schedule) { mutableStateOf(schedule.title) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPeriodModal by remember { mutableStateOf(false) }
    var editedDate by remember(schedule) { mutableStateOf(schedule.date) }
    val initialPeriodSelection = remember(schedule) { schedule.timeLabel.extractPeriodFlags() }
    var morningSelected by remember(schedule) { mutableStateOf(initialPeriodSelection.first) }
    var eveningSelected by remember(schedule) { mutableStateOf(initialPeriodSelection.second) }
    var timeLabel by remember(schedule) { mutableStateOf(schedule.timeLabel) }

    if (showDatePicker) {
        AddDateModal(
            initialDate = editedDate.toCalendar(),
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                editedDate = it.toLocalDate()
                showDatePicker = false
            }
        )
    }

    if (showPeriodModal) {
        SetPeriodModal(
            initialMorning = morningSelected,
            initialEvening = eveningSelected,
            onDismiss = { showPeriodModal = false },
            onDone = { morning, evening ->
                morningSelected = morning
                eveningSelected = evening
                timeLabel = when {
                    morning && evening -> "Morning, Evening"
                    morning -> "Morning"
                    evening -> "Evening"
                    else -> "All day"
                }
                showPeriodModal = false
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text("Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = scheduleName,
                    onValueChange = { scheduleName = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Sched date & Period", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Checked",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showDatePicker = true }
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Change date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            editedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showPeriodModal = true }
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Change period",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            timeLabel,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onDelete(schedule) }) {
                        Text("Delete", color = Color.Red)
                    }
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            val updatedReminder = schedule.reminderDateTime?.let { reminder ->
                                LocalDateTime.of(editedDate, reminder.toLocalTime())
                            }
                            onDone(
                                schedule.copy(
                                    title = scheduleName,
                                    date = editedDate,
                                    timeLabel = timeLabel,
                                    reminderDateTime = updatedReminder
                                )
                            )
                        }) {
                            Text("Done")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditScheduleModalPreview() {
    SchedlyTheme {
        EditScheduleModal(
            schedule = initialSchedules().first(),
            onDismiss = {},
            onDone = {},
            onDelete = {}
        )
    }
}

private fun String.extractPeriodFlags(): Pair<Boolean, Boolean> {
    val value = lowercase(Locale.getDefault())
    val hasMorning = value.contains("morning")
    val hasEvening = value.contains("evening")
    return hasMorning to hasEvening
}

private fun LocalDate.toCalendar(): Calendar = Calendar.getInstance().apply {
    set(year, monthValue - 1, dayOfMonth, 0, 0, 0)
    set(Calendar.MILLISECOND, 0)
}

private fun Calendar.toLocalDate(): LocalDate = LocalDate.of(
    get(Calendar.YEAR),
    get(Calendar.MONTH) + 1,
    get(Calendar.DAY_OF_MONTH)
)
