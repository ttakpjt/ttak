package com.ttak.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ttak.android.data.local.dao.FocusGoalDao
import com.ttak.android.data.local.dao.SelectedAppDao
import com.ttak.android.data.local.entity.FocusGoalEntity
import com.ttak.android.data.local.entity.SelectedAppEntity

@Database(
    entities = [FocusGoalEntity::class, SelectedAppEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun focusGoalDao(): FocusGoalDao
    abstract fun selectedAppDao(): SelectedAppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ttak_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}