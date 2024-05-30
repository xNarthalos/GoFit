package com.example.gofit.data

import androidx.room.Entity

@Entity(tableName = "user_data", primaryKeys = ["userId", "date"])
data class UserData(
    val userId: String,
    val date: String,
    val steps: Int,
    val distance: Float,
    val calories: Int,
    val score: Int
)
