package com.example.gofit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserData::class], version = 2, exportSchema = false)
abstract class GoFitDatabase : RoomDatabase() {
    // Define el DAO que se utilizará para acceder a los datos de la base de datos
    abstract fun userDataDao(): UserDataDao

    companion object {
        // La anotación @Volatile garantiza que los cambios realizados en esta variable sean visibles para todos los hilos
        @Volatile
        private var INSTANCE: GoFitDatabase? = null
        // Método para obtener una instancia de la base de datos
        fun getDatabase(context: Context): GoFitDatabase {
            // Si la instancia no es nula, se usa esa instancia. Si es nula, se crea la base de datos.
            return INSTANCE ?: synchronized(this) {
                // Bloque sincronizado para asegurarse de que solo un hilo puede ejecutar este bloque a la vez
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoFitDatabase::class.java,
                    "gofit_database"
                )   // Destruye la base de datos existente en caso de que haya una incompatibilidad de versiones, y crea una nueva
                    .fallbackToDestructiveMigration()

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
