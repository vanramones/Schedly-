package com.example.schedly.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScheduleCategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(crossRef: ScheduleCategoryCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(crossRefs: List<ScheduleCategoryCrossRef>)

    @Query("DELETE FROM schedule_category_cross_ref WHERE schedule_id = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM schedule_category_cross_ref)")
    suspend fun hasAny(): Boolean
}
