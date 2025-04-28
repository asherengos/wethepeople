package com.example.wethepeople.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wethepeople.data.local.dao.UserDao
import com.example.wethepeople.data.local.entities.UserData
import com.example.wethepeople.data.local.entities.OwnedItem
import com.example.wethepeople.data.local.entities.EquippedItem

@Database(
    entities = [
        UserData::class,
        OwnedItem::class,
        EquippedItem::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration() // For development only - remove in production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 