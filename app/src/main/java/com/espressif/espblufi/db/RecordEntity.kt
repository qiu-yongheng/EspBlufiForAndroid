package com.espressif.espblufi.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record")
data class RecordEntity(
    var date: Long,
    var uid: String,
    var mid: String,
    var other: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}