package com.example.schedly.data.local

import com.example.schedly.Schedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    fun observeSchedules(ownerUsername: String): Flow<List<Schedule>> =
        scheduleDao.observeSchedules(ownerUsername).map { entities -> entities.map { it.toDomain() } }

    suspend fun insert(schedule: Schedule): Int = withContext(Dispatchers.IO) {
        scheduleDao.insert(schedule.copy(id = 0).toEntity()).toInt()
    }

    suspend fun update(schedule: Schedule) = withContext(Dispatchers.IO) {
        scheduleDao.update(schedule.toEntity())
    }

    suspend fun delete(scheduleId: Int) = withContext(Dispatchers.IO) {
        scheduleDao.deleteById(scheduleId)
    }

    suspend fun seedDefaults(defaults: List<Schedule>): List<Pair<Schedule, Int>> = withContext(Dispatchers.IO) {
        val inserted = mutableListOf<Pair<Schedule, Int>>()
        defaults.groupBy { it.ownerUsername }.forEach { (owner, ownerDefaults) ->
            if (!scheduleDao.hasAny(owner)) {
                ownerDefaults.forEach { schedule ->
                    val id = scheduleDao.insert(schedule.copy(id = 0).toEntity()).toInt()
                    inserted += schedule to id
                }
            }
        }
        inserted
    }

    suspend fun getAll(): List<Schedule> = withContext(Dispatchers.IO) {
        scheduleDao.getAll().map { it.toDomain() }
    }
}

private fun Schedule.toEntity(): ScheduleEntity =
    ScheduleEntity(
        id = id,
        ownerUsername = ownerUsername,
        title = title,
        timeLabel = timeLabel,
        date = date,
        isCompleted = isCompleted,
        reminderDateTime = reminderDateTime
    )

private fun ScheduleEntity.toDomain(): Schedule =
    Schedule(
        id = id,
        ownerUsername = ownerUsername,
        title = title,
        timeLabel = timeLabel,
        date = date,
        isCompleted = isCompleted,
        reminderDateTime = reminderDateTime
    )
