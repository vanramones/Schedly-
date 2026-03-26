package com.example.schedly.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE schedule_id = :scheduleId ORDER BY reminder_time")
    fun observeReminders(scheduleId: Int): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteById(reminderId: Int)

    @Query("DELETE FROM reminders WHERE schedule_id = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM reminders)")
    suspend fun hasAny(): Boolean
}
