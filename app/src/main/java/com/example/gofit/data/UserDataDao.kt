package com.example.gofit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userData: UserData)
    @Update
    suspend fun update(userData: UserData)

    @Query("SELECT * FROM user_data WHERE userId = :userId AND date = :date")
    suspend fun getUserDataByDate(userId: String, date: String): UserData?

    @Query("SELECT SUM(score) FROM user_data WHERE userId = :uid")
    suspend fun getTotalScore(uid: String): Int?

    @Query("SELECT * FROM user_data WHERE userId = :userId")
    suspend fun getAllUserData(userId: String): List<UserData>

    @Query("DELETE FROM user_data WHERE userId = :userId AND date = :date")
    suspend fun deleteUserDataByDate(userId: String, date: String)

    @Query("DELETE FROM user_data")
    suspend fun deleteAllUserData()

    @Query("""
        SELECT * FROM user_data 
        WHERE userId = :userId 
        AND date BETWEEN :startDate AND :endDate
        ORDER BY date ASC
    """)
    suspend fun getUserDataForWeek(userId: String, startDate: String, endDate: String): List<UserData>
}
