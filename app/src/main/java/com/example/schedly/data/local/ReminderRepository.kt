package com.example.schedly.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ReminderRepository(private val reminderDao: ReminderDao) {

    fun observeReminders(scheduleId: Int): Flow<List<Reminder>> =
        reminderDao.observeReminders(scheduleId).map { entities -> entities.map { it.toDomain() } }

    suspend fun insert(scheduleId: Int, reminderTime: LocalDateTime, message: String?): Int = withContext(Dispatchers.IO) {
        reminderDao.insert(
            ReminderEntity(
                scheduleId = scheduleId,
                reminderTime = reminderTime,
                message = message
            )
        ).toInt()
    }

    suspend fun delete(reminderId: Int) = withContext(Dispatchers.IO) {
        reminderDao.deleteById(reminderId)
    }

    suspend fun deleteBySchedule(scheduleId: Int) = withContext(Dispatchers.IO) {
        reminderDao.deleteByScheduleId(scheduleId)
    }

    suspend fun hasAny(): Boolean = withContext(Dispatchers.IO) {
        reminderDao.hasAny()
    }
}

data class Reminder(
    val id: Int,
    val scheduleId: Int,
    val reminderTime: LocalDateTime,
    val message: String?
)

private fun ReminderEntity.toDomain(): Reminder = Reminder(
    id = id,
    scheduleId = scheduleId,
    reminderTime = reminderTime,
    message = message
)
