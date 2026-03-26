package com.example.schedly

import java.time.LocalDate
import java.time.LocalDateTime

data class Schedule(
    val id: Int,
    val ownerUsername: String,
    val title: String,
    val timeLabel: String,
    val date: LocalDate,
    val isCompleted: Boolean,
    val reminderDateTime: LocalDateTime? = null
)

fun initialSchedules(): List<Schedule> = listOf(
    Schedule(
        id = 1,
        ownerUsername = "demo",
        title = "Class 1",
        timeLabel = "9:00 AM - 11:00 AM",
        date = LocalDate.of(2025, 12, 18),
        isCompleted = true,
        reminderDateTime = LocalDateTime.of(2025, 12, 18, 8, 30)
    ),
    Schedule(
        id = 2,
        ownerUsername = "demo",
        title = "Class 2",
        timeLabel = "2:00 PM - 4:00 PM",
        date = LocalDate.of(2025, 12, 18),
        isCompleted = false,
        reminderDateTime = null
    ),
    Schedule(
        id = 3,
        ownerUsername = "demo",
        title = "Team Sync",
        timeLabel = "10:00 AM - 11:30 AM",
        date = LocalDate.of(2025, 12, 24),
        isCompleted = false,
        reminderDateTime = null
    )
)
