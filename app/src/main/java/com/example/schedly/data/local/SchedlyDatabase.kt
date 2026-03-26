package com.example.schedly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ScheduleEntity::class,
        UserEntity::class,
        ReminderEntity::class,
        CategoryEntity::class,
        ScheduleCategoryCrossRef::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class SchedlyDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
    abstract fun userDao(): UserDao
    abstract fun reminderDao(): ReminderDao
    abstract fun categoryDao(): CategoryDao
    abstract fun scheduleCategoryDao(): ScheduleCategoryDao

    companion object {
        @Volatile
        private var Instance: SchedlyDatabase? = null

        fun getInstance(context: Context): SchedlyDatabase =
            Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SchedlyDatabase::class.java,
                    "schedly.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build().also { Instance = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE schedules ADD COLUMN owner_username TEXT NOT NULL DEFAULT 'legacy'"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS schedules_new (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            owner_username TEXT NOT NULL,
                            title TEXT NOT NULL,
                            time_label TEXT NOT NULL,
                            date TEXT NOT NULL,
                            is_completed INTEGER NOT NULL,
                            reminder_date_time TEXT
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                        INSERT INTO schedules_new (
                            id,
                            owner_username,
                            title,
                            time_label,
                            date,
                            is_completed,
                            reminder_date_time
                        )
                        SELECT
                            id,
                            owner_username,
                            title,
                            timeLabel,
                            date,
                            is_completed,
                            reminder_date_time
                        FROM schedules
                    """.trimIndent()
                )
                database.execSQL("DROP TABLE schedules")
                database.execSQL("ALTER TABLE schedules_new RENAME TO schedules")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS reminders (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            schedule_id INTEGER NOT NULL,
                            reminder_time TEXT NOT NULL,
                            message TEXT,
                            FOREIGN KEY(schedule_id) REFERENCES schedules(id) ON DELETE CASCADE
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_reminders_schedule_id ON reminders(schedule_id)"
                )

                database.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS categories (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_categories_name ON categories(name)"
                )

                database.execSQL(
                    """
                        CREATE TABLE IF NOT EXISTS schedule_category_cross_ref (
                            schedule_id INTEGER NOT NULL,
                            category_id INTEGER NOT NULL,
                            PRIMARY KEY(schedule_id, category_id),
                            FOREIGN KEY(schedule_id) REFERENCES schedules(id) ON DELETE CASCADE,
                            FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE
                        )
                    """.trimIndent()
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_schedule_category_cross_ref_category_id ON schedule_category_cross_ref(category_id)"
                )
            }
        }
    }
}
