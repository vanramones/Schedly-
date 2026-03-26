package com.example.schedly

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedly.Schedule
import java.time.LocalDate
import java.time.LocalDateTime
import com.example.schedly.ui.theme.FrauncesFamily
import com.example.schedly.ui.theme.SchedlyTheme
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewSchedScreen(
    onBackClick: () -> Unit,
    ownerUsername: String,
    onSaveSchedule: (Schedule) -> Unit
) {
    var schedName by remember { mutableStateOf("") }
    var getReminders by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var showAddDateModal by remember { mutableStateOf(false) }
    var showSetPeriodModal by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var morningSelection by remember { mutableStateOf(false) }
    var eveningSelection by remember { mutableStateOf(false) }

    if (showAddDateModal) {
        AddDateModal(
            onDismiss = { showAddDateModal = false },
            onDateSelected = {
                selectedDate = it
                showAddDateModal = false
            }
        )
    }

    if (showSetPeriodModal) {
        SetPeriodModal(
            onDismiss = { showSetPeriodModal = false },
            onDone = { morning, evening ->
                morningSelection = morning
                eveningSelection = evening
            }
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { selectedTime = it }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Sched", fontFamily = FrauncesFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(id = R.drawable.illustration_bell), contentDescription = "Bell Illustration", modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Image(painter = painterResource(id = R.drawable.illustration_pencil_holder), contentDescription = "Pencil Holder Illustration", modifier = Modifier.size(100.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Name", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = schedName,
                onValueChange = { schedName = it },
                placeholder = { Text("Enter your sched") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Set Sched date & Period", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Icon(painter = painterResource(id = R.drawable.icon_check_blue), contentDescription = "Done", modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))

            val dateButtonText = selectedDate?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it.time)
            } ?: "+ Add date"

            val periodButtonText = when {
                morningSelection && eveningSelection -> "Morning, Evening"
                morningSelection -> "Morning"
                eveningSelection -> "Evening"
                else -> "+ Add Period"
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CircularIconButton(iconRes = R.drawable.icon_calendar_blue, text = dateButtonText, onClick = { showAddDateModal = true })
                CircularIconButton(iconRes = R.drawable.icon_add_star, text = periodButtonText, onClick = { showSetPeriodModal = true })
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Get reminders", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Switch(checked = getReminders, onCheckedChange = {
                    getReminders = it
                    if (it) {
                        showTimePicker = true
                    }
                })
            }
            selectedTime?.let {
                Text(
                    text = "Reminder at: ${it.format(DateTimeFormatter.ofPattern("hh:mm a"))}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val date = selectedDate?.let {
                        LocalDate.of(it.get(Calendar.YEAR), it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH))
                    } ?: return@Button

                    if (schedName.isBlank()) {
                        return@Button
                    }

                    val reminderDateTime = if (getReminders) {
                        selectedTime?.let { LocalDateTime.of(date, it) } ?: run {
                            showTimePicker = true
                            return@Button
                        }
                    } else null

                    val timeLabel = when {
                        selectedTime != null -> selectedTime?.format(DateTimeFormatter.ofPattern("hh:mm a")) ?: "All day"
                        morningSelection || eveningSelection -> buildList {
                            if (morningSelection) add("Morning")
                            if (eveningSelection) add("Evening")
                        }.joinToString(", ")
                        else -> "All day"
                    }

                    val schedule = Schedule(
                        id = 0,
                        ownerUsername = ownerUsername,
                        title = schedName,
                        timeLabel = timeLabel,
                        date = date,
                        isCompleted = false,
                        reminderDateTime = reminderDateTime
                    )

                    onSaveSchedule(schedule)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(painter = painterResource(id = R.drawable.icon_check_blue), contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save this Sched", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CircularIconButton(iconRes: Int, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNewSchedScreen() {
    SchedlyTheme {
        NewSchedScreen(onBackClick = {}, ownerUsername = "demo", onSaveSchedule = {})
    }
}
