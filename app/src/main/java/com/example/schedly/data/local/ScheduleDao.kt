package com.example.schedly.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedules WHERE owner_username = :ownerUsername ORDER BY date")
    fun observeSchedules(ownerUsername: String): Flow<List<ScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: ScheduleEntity): Long

    @Update
    suspend fun update(schedule: ScheduleEntity)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM schedules WHERE owner_username = :ownerUsername)")
    suspend fun hasAny(ownerUsername: String): Boolean

    @Query("SELECT * FROM schedules")
    suspend fun getAll(): List<ScheduleEntity>
}
