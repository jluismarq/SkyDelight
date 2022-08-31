package com.example.skydelight.custom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "sex") var sex: String,
    @ColumnInfo(name = "age") var age: Int,
    @ColumnInfo(name = "token") var token: String,
    @ColumnInfo(name = "refresh") var refresh: String,
    @ColumnInfo(name = "session") var session: Boolean,
    @ColumnInfo(name = "advice") var advice: Boolean = true
)