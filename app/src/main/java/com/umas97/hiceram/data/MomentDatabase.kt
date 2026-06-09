package com.umas97.hiceram.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Il database Room dell'applicazione.
 */
@Database(entities = [Moment::class], version = 4, exportSchema = false)
abstract class MomentDatabase : RoomDatabase() {

    abstract fun momentDao(): MomentDao

    companion object {
        @Volatile
        private var Instance: MomentDatabase? = null

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE moments ADD COLUMN locationName TEXT")
                db.execSQL("ALTER TABLE moments ADD COLUMN latitude REAL")
                db.execSQL("ALTER TABLE moments ADD COLUMN longitude REAL")
            }
        }

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
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
}
