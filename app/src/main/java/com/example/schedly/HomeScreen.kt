package com.example.schedly

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedly.ui.theme.FrauncesFamily
import com.example.schedly.ui.theme.InstrumentSansFamily
import com.example.schedly.ui.theme.SchedlyTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    schedules: List<Schedule>,
    userName: String,
    onAddSchedClick: () -> Unit,
    onProfileClick: () -> Unit,
    onToggleScheduleCompletion: (scheduleId: Int, isCompleted: Boolean) -> Unit,
    onUpdateSchedule: (Schedule) -> Unit,
    onDeleteSchedule: (scheduleId: Int) -> Unit
) {
    var showHomeCalendarModal by remember { mutableStateOf(false) }
    var showEditScheduleModal by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentScreen by remember { mutableStateOf("Home") }
    var scheduleBeingEdited by remember { mutableStateOf<Schedule?>(null) }

    val scheduledDates = remember(schedules) { schedules.map { it.date }.toSet() }

    if (showHomeCalendarModal) {
        HomeCalendarModal(
            initialDate = selectedDate,
            scheduledDates = scheduledDates,
            onDismiss = {
                showHomeCalendarModal = false
                currentScreen = "Home"
            },
            onDateSelected = {
                selectedDate = it
                showHomeCalendarModal = false
                currentScreen = "Home"
            }
        )
    }

    if (showEditScheduleModal) {
        scheduleBeingEdited?.let { schedule ->
            EditScheduleModal(
                schedule = schedule,
                onDismiss = {
                    showEditScheduleModal = false
                    scheduleBeingEdited = null
                },
                onDone = { updated ->
                    onUpdateSchedule(updated)
                    showEditScheduleModal = false
                    scheduleBeingEdited = null
                },
                onDelete = { toDelete ->
                    onDeleteSchedule(toDelete.id)
                    showEditScheduleModal = false
                    scheduleBeingEdited = null
                }
            )
        }
    }

    SchedlyTheme {
        val currentDate = selectedDate
        val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())
        val fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM, yyyy", Locale.getDefault())

        val dayOfWeek = currentDate.format(dayOfWeekFormatter)
        val fullDate = currentDate.format(fullDateFormatter)
        val schedulesForSelectedDate = schedules.filter { it.date == currentDate }
        val completedSchedules = schedulesForSelectedDate.filter { it.isCompleted }
        val upcomingSchedules = schedulesForSelectedDate.filter { !it.isCompleted }

        Scaffold(
            containerColor = Color.White,
            bottomBar = { SchedlyBottomNav(
                currentScreen = currentScreen,
                onHomeClick = { currentScreen = "Home" },
                onCalendarClick = {
                    showHomeCalendarModal = true
                    currentScreen = "Calendar"
                },
                onProfileClick = onProfileClick)
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddSchedClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Image(painter = painterResource(id = R.drawable.icon_add), contentDescription = "Add", modifier = Modifier.size(28.dp))
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Good evening 👋",
                            fontFamily = InstrumentSansFamily,
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                        Text(
                            text = userName,
                            fontFamily = FrauncesFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Today's Schedule Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showHomeCalendarModal = true
                            currentScreen = "Calendar"
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .height(IntrinsicSize.Min), // Ensures Row height is based on content
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(dayOfWeek, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontFamily = InstrumentSansFamily)
                            Text(fullDate, color = Color.White, fontSize = 14.sp, fontFamily = InstrumentSansFamily, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Today's\nSched",
                                fontFamily = FrauncesFamily,
                                color = Color.White,
                                fontSize = 36.sp,
                                lineHeight = 40.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Vertical Divider
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(Color.White.copy(alpha = 0.5f))
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Completed:", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = InstrumentSansFamily)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${completedSchedules.size} of ${schedulesForSelectedDate.size}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp,
                                fontFamily = InstrumentSansFamily
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (completedSchedules.isEmpty()) {
                                Text(
                                    "No completed schedules",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontFamily = InstrumentSansFamily
                                )
                            } else {
                                completedSchedules.forEach { schedule ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier
                                            .padding(bottom = 8.dp)
                                            .clickable { onToggleScheduleCompletion(schedule.id, false) }
                                    ) {
                                        RadioButton(
                                            selected = true,
                                            onClick = { onToggleScheduleCompletion(schedule.id, false) },
                                            colors = RadioButtonDefaults.colors(selectedColor = Color.White, unselectedColor = Color.White)
                                        )
                                        Text(
                                            schedule.title,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = InstrumentSansFamily
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Schedules for this day",
                    fontFamily = FrauncesFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (upcomingSchedules.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 0.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF7F8FA)
                    ) {
                        Text(
                            "No schedules yet. Tap the + button to add one!",
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontFamily = InstrumentSansFamily
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        upcomingSchedules.forEach { schedule ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scheduleBeingEdited = schedule
                                        showEditScheduleModal = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(schedule.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = InstrumentSansFamily)
                                        Text(
                                            schedule.timeLabel,
                                            color = Color.Gray,
                                            fontSize = 14.sp,
                                            fontFamily = InstrumentSansFamily
                                        )
                                    }
                                    Checkbox(
                                        checked = schedule.isCompleted,
                                        onCheckedChange = { isChecked ->
                                            onToggleScheduleCompletion(schedule.id, isChecked)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SchedlyBottomNav(currentScreen: String, onHomeClick: () -> Unit, onCalendarClick: () -> Unit, onProfileClick: () -> Unit) {
    BottomAppBar(
        containerColor = Color.White,
        tonalElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationBarItem(
                selected = currentScreen == "Home",
                onClick = onHomeClick,
                icon = { Icon(painterResource(id = R.drawable.icon_home), contentDescription = "Home", modifier = Modifier.size(28.dp)) }
            )
            NavigationBarItem(
                selected = currentScreen == "Calendar",
                onClick = onCalendarClick,
                icon = { Icon(painterResource(id = R.drawable.icon_calendar), contentDescription = "Calendar", modifier = Modifier.size(28.dp)) }
            )
            NavigationBarItem(
                selected = currentScreen == "Profile",
                onClick = onProfileClick,
                icon = { Icon(painterResource(id = R.drawable.icon_profilecircle), contentDescription = "Profile", modifier = Modifier.size(28.dp)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    SchedlyTheme {
        HomeScreen(
            schedules = initialSchedules(),
            userName = "Schedly User",
            onAddSchedClick = {},
            onProfileClick = {},
            onToggleScheduleCompletion = { _, _ -> },
            onUpdateSchedule = {},
            onDeleteSchedule = {}
        )
    }
}
