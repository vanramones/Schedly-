package com.example.schedly.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "owner_username") val ownerUsername: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "time_label") val timeLabel: String,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo(name = "reminder_date_time") val reminderDateTime: LocalDateTime?
)
