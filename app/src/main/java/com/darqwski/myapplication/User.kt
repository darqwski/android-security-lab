package com.darqwski.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var uid: Int? = null,
    @ColumnInfo(name = "user_name") var userName: String,
    @ColumnInfo(name = "number") var number: Int
)