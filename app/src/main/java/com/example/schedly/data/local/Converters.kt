package com.example.schedly.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime

class DateConverters {
    @TypeConverter
    fun fromStringToLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalDateToString(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun fromStringToLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromLocalDateTimeToString(dateTime: LocalDateTime?): String? = dateTime?.toString()
}
