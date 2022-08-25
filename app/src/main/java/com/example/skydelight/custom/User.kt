package com.example.skydelight.custom

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val email: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "sex") val sex: String,
    @ColumnInfo(name = "age") val age: Int,
    @ColumnInfo(name = "token") val token: String,
    @ColumnInfo(name = "refresh") val refresh: String
)