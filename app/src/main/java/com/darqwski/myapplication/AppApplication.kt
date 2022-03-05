package com.darqwski.myapplication

import android.app.Application
import androidx.room.Room

class AppApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
    }
}