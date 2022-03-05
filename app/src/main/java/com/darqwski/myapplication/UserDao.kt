package com.darqwski.myapplication

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>
    @Query("SELECT * FROM user")
    fun getAllCursor(): Cursor
    @Insert
    fun insert(user: User)
}