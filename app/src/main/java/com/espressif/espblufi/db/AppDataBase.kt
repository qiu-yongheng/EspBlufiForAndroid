package com.espressif.espblufi.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author 邱永恒
 *
 * @time 2021/5/25  00:11
 *
 * @desc
 *
 */
@Database(
    entities = [
        RecordEntity::class
    ], version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    companion object {
        private var dbName = "esp"
        private var db: AppDataBase? = null

        fun instance(context: Context): AppDataBase {
            if (db == null) {
                synchronized(AppDataBase::class.java) {
                    if (db == null) {
                        db = Room.databaseBuilder(context, AppDataBase::class.java, dbName)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build()
                    }
                }
            }
            return db!!
        }
    }

    abstract fun recordDao(): RecordDao
}