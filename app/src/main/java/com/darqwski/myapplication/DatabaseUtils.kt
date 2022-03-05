package com.darqwski.myapplication

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseUtils {

    companion object {
        lateinit var db: AppDatabase

        fun initDatabase(applicationContext: Context) {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
        }
    }
}