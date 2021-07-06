package com.espressif.espblufi.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Long,
    val machineID: String,
    val other: String = ""
)