package com.example.schedly.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScheduleCategoryRepository(private val scheduleCategoryDao: ScheduleCategoryDao) {

    suspend fun insertAll(crossRefs: List<ScheduleCategoryCrossRef>) = withContext(Dispatchers.IO) {
        scheduleCategoryDao.insertAll(crossRefs)
    }

    suspend fun clearForSchedule(scheduleId: Int) = withContext(Dispatchers.IO) {
        scheduleCategoryDao.deleteByScheduleId(scheduleId)
    }

    suspend fun hasAny(): Boolean = withContext(Dispatchers.IO) {
        scheduleCategoryDao.hasAny()
    }
}
