package com.espressif.espblufi.db

import androidx.room.*

@Dao
interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecord(entity: RecordEntity)

    @Delete
    fun deleteRecord(entity: RecordEntity)

    @Query("DELETE FROM record")
    fun deleteAll()

    @Query("SELECT * FROM record ORDER BY id DESC")
    fun getAllRecord(): List<RecordEntity>
}