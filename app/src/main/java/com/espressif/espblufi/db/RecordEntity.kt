package com.espressif.espblufi.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity(
    val date: Long,
    val uid: String,
    val mid: String,
    val other: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}