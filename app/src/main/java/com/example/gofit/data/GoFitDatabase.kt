package com.example.gofit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserData::class], version = 2, exportSchema = false)
abstract class GoFitDatabase : RoomDatabase() {
    abstract fun userDataDao(): UserDataDao

    companion object {
        @Volatile
        private var INSTANCE: GoFitDatabase? = null

        fun getDatabase(context: Context): GoFitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoFitDatabase::class.java,
                    "gofit_database"
                )// Esta línea permite recrear la base de datos si hay un cambio en el esquema
                    .fallbackToDestructiveMigration()

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
