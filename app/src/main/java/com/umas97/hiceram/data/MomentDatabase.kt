package com.umas97.hiceram.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Il database Room dell'applicazione.
 */
@Database(entities = [Moment::class], version = 3, exportSchema = false)
abstract class MomentDatabase : RoomDatabase() {

    abstract fun momentDao(): MomentDao

    companion object {
        @Volatile
        private var Instance: MomentDatabase? = null

        /**
         * Ottiene l'istanza singleton del database.
         */
        fun getDatabase(context: Context): MomentDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MomentDatabase::class.java,
                    "moment_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
